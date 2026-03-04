package com.SIGMA.USCO.documents.repository;

import com.SIGMA.USCO.documents.entity.DocumentEditRequestVote;
import com.SIGMA.USCO.documents.entity.enums.EditRequestVoteDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentEditRequestVoteRepository extends JpaRepository<DocumentEditRequestVote, Long> {

    List<DocumentEditRequestVote> findByEditRequestId(Long editRequestId);

    Optional<DocumentEditRequestVote> findByEditRequestIdAndExaminerId(Long editRequestId, Long examinerId);

    boolean existsByEditRequestIdAndExaminerId(Long editRequestId, Long examinerId);

    long countByEditRequestIdAndDecision(Long editRequestId, EditRequestVoteDecision decision);

    List<DocumentEditRequestVote> findByEditRequestIdAndIsTiebreakerVote(Long editRequestId, Boolean isTiebreakerVote);
}

