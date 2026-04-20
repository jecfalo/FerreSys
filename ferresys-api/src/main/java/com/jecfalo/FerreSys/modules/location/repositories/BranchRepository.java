package com.jecfalo.FerreSys.modules.location.repositories;

import com.jecfalo.FerreSys.modules.location.models.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
