package com.app.onlinejobportal.repository;

import com.app.onlinejobportal.model.BlacklistedTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokensRepository extends JpaRepository<BlacklistedTokens, Long> {
}
