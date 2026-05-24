    package com.jecfalo.FerreSys.modules.sales.services;

    import com.jecfalo.FerreSys.modules.inventory.models.Product;
    import com.jecfalo.FerreSys.modules.inventory.repositories.ProductRepository;
    import com.jecfalo.FerreSys.modules.inventory.services.IInventoryService;
    import com.jecfalo.FerreSys.modules.location.models.StorageRoom;
    import com.jecfalo.FerreSys.modules.location.repositories.StorageRoomRepository;
    import com.jecfalo.FerreSys.modules.sales.models.CashRegister;
    import com.jecfalo.FerreSys.modules.sales.models.Order;
    import com.jecfalo.FerreSys.modules.sales.models.OrderDetail;
    import com.jecfalo.FerreSys.modules.sales.models.PaidState;
    import com.jecfalo.FerreSys.modules.sales.records.order.*;
    import com.jecfalo.FerreSys.modules.sales.repositories.CashRegisterRepository;
    import com.jecfalo.FerreSys.modules.sales.repositories.OrderRepository;
    import com.jecfalo.FerreSys.modules.users.models.Profile;
    import com.jecfalo.FerreSys.modules.users.repositories.ProfileRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.List;
    /**
     * Servicio principal para la gestión de las órdenes de venta.
     * Implementa la lógica de negocio para crear, actualizar, listar y eliminar órdenes,
     * interactuando con el inventario y las cajas registradoras.
     */
    @Service
    public class OrderService implements IOrderService{
        @Autowired
        private OrderRepository repository;
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private StorageRoomRepository storageRepository;
        @Autowired
        private ProfileRepository profileRepository;
        @Autowired
        private CashRegisterRepository cashRegisterRepository;
        @Autowired
        private IInventoryService inventoryService;
        /**
         * Obtiene un listado paginado de todas las órdenes activas en el sistema.
         * Tiene en cuenta los permisos del usuario autenticado:
         * - Si es cliente (ROLE_CLIENT), solo retorna sus propias órdenes.
         * - Si es un rol administrativo (ROLE_ADMIN, ROLE_SELLER), retorna todas las órdenes.
         * 
         * @param page Configuración de la paginación solicitada por el cliente.
         * @return Página de DataListOrder con las órdenes correspondientes.
         */
        @Override
        @Transactional
        public Page<DataListOrder> loadAllActive(Pageable page) {
            // Obtenemos el contexto de seguridad actual para determinar quién hace la petición
            var auth = SecurityContextHolder.getContext().getAuthentication();
            String typeProfile = auth.getAuthorities().iterator().next().getAuthority();
            
            // Consultamos todas las órdenes de la base de datos de manera paginada
            Page<Order>  orders = repository.findAll(page);
            
            if(typeProfile.equals("ROLE_CLIENT")){
                // Filtramos exclusivamente las órdenes que le pertenecen al cliente basándonos en su nombre de usuario
                Page<Order> orderClient = repository.findByClient_User_Username(auth.getName(), page);
                return orderClient.map(DataListOrder::new);
            }
            
            // Si no es cliente, mapeamos todas las órdenes encontradas al DTO correspondiente y retornamos
            return orders.map(DataListOrder::new);
        }

        /**
         * Crea y procesa una nueva orden de venta (presupuesto inicial "Pendiente").
         * Este flujo incluye la asignación del cliente, vendedor y caja, y luego realiza
         * la reserva de inventario para cada producto listado en la compra.
         * 
         * @param register Contiene los datos necesarios para generar la orden junto con el detalle de los productos.
         * @return ReferenceOrder DTO de la orden creada incluyendo los cálculos iniciales financieros.
         */
        @Override
        @Transactional
        public ReferenceOrder create(RegisterOrder register) {
            // Instanciamos una nueva entidad Order a partir de los datos recibidos (aún sin persistir)
            Order order = new Order(register);
            
            // Buscamos y asignamos el perfil del cliente que está realizando la compra
            Profile client = profileRepository.findById(register.clientId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado en la base de datos"));
            order.setClient(client);
            
            // Verificamos y asignamos el vendedor (si es provisto, usualmente para compras presenciales)
            if(register.sellerId() != null){
                Profile seller =  profileRepository.findById(register.sellerId())
                        .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
                order.setSeller(seller);
            }
            
            // Verificamos si se ha especificado la caja registradora de la transacción
            if(register.cashRegisterId() != null){
                CashRegister cash = cashRegisterRepository.findById(register.cashRegisterId())
                        .orElseThrow(()->new RuntimeException("No se encontro tal caja registradora en el sistema"));
            }
            
            // Lista para almacenar la relación con cada uno de los productos que componen la compra
            List<OrderDetail> details = new ArrayList<>();
            // Variable acumulativa para el total a pagar en la factura
            double totalCalculated = 0.0;
            
            // Recorremos la canasta de compras (array de detalles) enviada en la solicitud principal
            for(RegisterOrderDetail detail: register.details()){
                // Buscamos las entidades de Producto y Bodega asociadas al detalle
                Product product = productRepository.findById(detail.productId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado. ID: " + detail.productId()));
                StorageRoom storage = storageRepository.findById(detail.storageId())
                        .orElseThrow(() -> new RuntimeException("Bodega no encontrada. ID: " + detail.storageId()));
                
                // Procedemos a realizar la "Reserva" en el inventario antes de guardar todo (bloquea el item)
                inventoryService.eserveStock(product.getId(), storage.getId(), detail.quantity());
                
                // Cálculo financiero: acumulamos -> Precio unitario * la cantidad solicitada
                totalCalculated += (product.getPrice() * detail.quantity());
                
                // Generamos la entidad relacional para la DB y la agrupamos
                OrderDetail detailNew = new OrderDetail(detail, order, product, storage);
                details.add(detailNew);
            }
            
            // Configuración final de la cabecera de la factura con el gran total derivado de iterar los detalles
            order.setTotalAmount(totalCalculated);
            // La inicializamos como pendiente (en 0.0 el monto y vuelto) porque aún no ha sido pagada
            order.setAmountPaid(0.0);
            order.setChangeDue(0.0);
            
            // IMPORTANTE:
            // OrderDetails es hijo de Order. Para que exista el detalle, necesitamos setear esta lista
            // en la entidad Order actual. Si guardamos la orden, Hibernate de manera automática persistirá
            // los detalles en cascada en la base de datos en una sola transacción sin necesidad expresa 
            // de OrderDetailRepository siempre y cuando esté configurado cascade = CascadeType.ALL.
            order.setOrderDetail(details);
            
            Order saveOrder = repository.save(order);
            
            // Retornamos el DTO de respuesta transformando la entidad final guardada
            return new ReferenceOrder(saveOrder);
        }

        @Override
        @Transactional
        public ReferenceOrder getOrderById(Long id) {
            Order order = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("order not found"));
            validateOrderOwnership(order);//fachada de seguridad
            return new ReferenceOrder(order);
        }

        /**
         * Actualiza una orden existente. Particularmente utilizado para procesar el pago 
         * o para cancelar una orden. Si se paga, realiza un descargo definitivo de inventario y 
         * maneja operaciones de vuelto.
         * 
         * @param id Identificador de la orden a actualizar
         * @param update Objeto que incluye el nuevo estado y el dinero entregado.
         * @return ReferenceOrder DTO que refleja el estado actual finalizado.
         */
        @Override
        @Transactional
        public ReferenceOrder update(Long id, UpdateOrder update) {
            // Protección de null explícita para evitar NullPointerException inesperadas con el enum
            if(update.state() == null){
                throw new RuntimeException("El nuevo estado suministrado está vacío. Por favor verifique los datos enviados.");
            }
            
            // Verificamos si existe dicha orden en la BDD para ser tratada
            Order order  = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Orden de venta no encontrada. ID: " + id));
            validateOrderOwnership(order); // Seguridad: verificar que el usuario tiene acceso a esta orden
            
            // Obtenemos el flujo de estados
            PaidState state =  order.getPaidState();     // Su estado previo en BDD
            PaidState newState = update.state();         // El estado que requiere el FrontEnd

            // Flujo 1: Se va a liquidar el pago y finalizar un presupuesto PENDING a PAID
            if(state == PaidState.PENDING && newState == PaidState.PAID){
                // Preguntamos quién realiza la petición usando Spring Security context
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String profileType = authentication.getAuthorities().iterator().next().getAuthority();
                
                // Impedimos logísticamente que un cliente (rol) finalice compras a voluntad o piratee el punto de pago online
                if(profileType.equals("ROLE_CLIENT")){
                    throw new RuntimeException("ALERTA DE SEGURIDAD: Un cliente no cuenta con permisos para marcar una orden como pagada. Acérquese a caja registradora, por favor.");
                }
                
                // Validación del monto ingresado en caja
                if(update.amountPaid() == null){
                    throw new RuntimeException("Para procesar un cobro, debe indicar obligatoriamente la cantidad de dinero recibido por el cliente (amountPaid)");
                }
                
                // Calculamos el vuelto comparando el dinero recibido Vs total
                double change = update.amountPaid() - order.getTotalAmount();
                if(change < 0){
                    // Bloqueo duro: Nunca permitir pagos parciales si no se configuran cuotas ("fiado")
                    throw new RuntimeException("Fondos insuficientes reportados. El costo total asciende a $" + order.getTotalAmount()
                            + ", pero tan solo ha entregado $" + update.amountPaid());
                }
                
                // Actualización financiera del comprobante
                order.setAmountPaid(update.amountPaid());
                order.setChangeDue(change);
                
                // Proceso vital: Al ser pagado, se confirman las reducciones de los ítems en su bodega específica 
                // Esto transforma un "inventario reservado" a ser definitivamente extraído y bajado del stock general
                for(OrderDetail detail: order.getOrderDetail()){
                    inventoryService.confirmSale(detail.getProduct().getId(), detail.getStorage().getId(), detail.getQuantity());
                }
                
            // Flujo 2: Un presupuesto es descartado o cancelado y revertido de PENDING a CANCELLED
            }else if(state == PaidState.PENDING && newState == PaidState.CANCELLED){
                // Libera todas las unidades temporalmente trancadas por reservas de compra en curso para volver a ponerlas comerciales
                for(OrderDetail detail: order.getOrderDetail()){
                    inventoryService.releasedStock(detail.getProduct().getId(), detail.getStorage().getId(), detail.getQuantity());
                }
            }
            
            // Marcado final del estado del Enum para guardarse
            order.setPaidState(newState);
            repository.save(order);
            
            return new ReferenceOrder(order);
        }

        @Override
        @Transactional
        public void deleteOrder(Long id) {
            //buscar si existe
            Order order = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("order not found"));
            validateOrderOwnership(order); // Seguridad: verificar que el usuario tiene acceso a esta orden
            if(order.getPaidState() == PaidState.PENDING){
                for(OrderDetail detail: order.getOrderDetail()){
                    inventoryService.releasedStock(detail.getProduct().getId(), detail.getStorage().getId(), detail.getQuantity());
                }
                order.setPaidState(PaidState.CANCELLED);
            }
            order.setActive(false);
            repository.save(order);
        }
        private void validateOrderOwnership(Order order){
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String typeProfile = authentication.getAuthorities().iterator().next().getAuthority();
            //los admin y seller tienen pase libre
            if(typeProfile.equals("ROLE_ADMIN") || typeProfile.equals("ROLE_SELLER")){
                return;
            }
            //el cliente solo ve lo suyo
            if(typeProfile.equals("ROLE_CLIENT")){
                if(order.getClient() == null || !order.getClient().getUser().getUsername().equals(username)){
                    throw new RuntimeException("No tienes permismo para acceder a este rrecurso");
                }
            }else{
                //cualquier otro rol no identificado se bloquea
                throw new RuntimeException("Tipo de usuario no permitido");
            }
        }
    }
