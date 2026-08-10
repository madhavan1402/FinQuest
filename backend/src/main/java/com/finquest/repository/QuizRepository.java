package com.finquest.repository;

import com.finquest.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizQuestion, Long> {

    // Fetches all questions for a given level.
    // Spring Data generates: SELECT * FROM quiz_questions WHERE level = ?
    List<QuizQuestion> findByLevel(int level);
<<<<<<< HEAD

    // Fetches only ACTIVE questions for a level (used by the secure quiz API).
    List<QuizQuestion> findBylevelAndActiveTrue(int level);

    // Count active questions for a level.
    long countBylevelAndActiveTrue(int level);
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
}
