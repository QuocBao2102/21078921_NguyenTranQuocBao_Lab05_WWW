package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Candidate;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Skill;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Page<Candidate> findAllByStatus(int status, Pageable pageable);

    List<Candidate> findByCandidateSkills_Skill_Id(Long skillId);
    Candidate findByCandidateLoginAndCandidatePassword(String candidateLogin, String candidatePassword);
    @Query("SELECT c FROM Candidate c JOIN c.candidateSkills cs WHERE cs.skill.id = :skillId")
    List<Candidate> findCandidatesBySkillId(@Param("skillId") Long skillId);

    @Query("SELECT c FROM Candidate c JOIN c.candidateSkills cs WHERE cs.skill.id = :skillId")
    List<Candidate> findBySkillId(@Param("skillId") Long skillId);

    @Query("SELECT c FROM Candidate c JOIN c.candidateSkills cs WHERE cs.skill IN :skills")
    List<Candidate> findCandidatesBySkills(@Param("skills") List<Skill> skills);
@Query("SELECT c FROM Candidate c JOIN c.candidateSkills cs WHERE cs.skill IN :skills AND c.address.city = :city")
List<Candidate> findCandidatesBySkillsAndLocation(@Param("skills") List<Skill> skills, @Param("city") String city);

}
