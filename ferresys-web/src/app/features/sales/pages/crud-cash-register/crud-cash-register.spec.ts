import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudCashRegister } from './crud-cash-register';

describe('CrudCashRegister', () => {
  let component: CrudCashRegister;
  let fixture: ComponentFixture<CrudCashRegister>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrudCashRegister]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrudCashRegister);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
