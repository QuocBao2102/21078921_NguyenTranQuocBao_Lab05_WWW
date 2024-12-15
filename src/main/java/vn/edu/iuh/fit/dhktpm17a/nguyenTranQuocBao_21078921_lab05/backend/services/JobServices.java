package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Candidate;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Job;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.JobSkill;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Skill;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.JobRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.JobSkillRepository;

import java.util.List;

@Service
public class JobServices {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private JobSkillRepository jobSkillRepository;
    @Autowired
    private SkillServices skillService;

    public void saveJob(Job job) {
        jobRepository.save(job);
    }
//    public List<Job> getJobsForCandidate(Long candID) {
//        return jobRepository.findByCandidateId(candID);
//    }
    public List<Job> getJobsBySkill(Long skillId) {
        return jobRepository.findBySkillId(skillId);
    }
    public List<Job> getAllJobs() {
        return jobRepository.findAll(); // Truy vấn tất cả công việc
    }
    public List<Job> searchJobsByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return jobRepository.findAll(); // Trả về tất cả công việc nếu từ khóa trống
        }
        return jobRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }
    // Tìm kiếm công việc theo từ khóa
    public List<Job> searchJobs(String keyword) {
        return jobRepository.findAll().stream()
                .filter(job -> job.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
    public Job getJobById(Long jobId) throws EntityNotFoundException {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));
    }

    // Lấy thông tin công việc theo ID
    public Job getJobDetails(Long jobId) {
        return jobRepository.findById(jobId).orElse(null);
    }

    // Lấy kỹ năng của công việc
    public List<Skill> getJobSkills(Long jobId) {
        return jobRepository.findSkillsByJobId(jobId);
    }
    public void createJob(Job job, List<Long> skillIds) {
        // Lưu công việc
        jobRepository.save(job);

        // Lấy danh sách kỹ năng từ ID
        List<Skill> skills = skillService.getSkillsByIds(skillIds);

        // Tạo liên kết JobSkill
        for (Skill skill : skills) {
            JobSkill jobSkill = new JobSkill();
            jobSkill.setJob(job);
            jobSkill.setSkill(skill);
            jobSkillRepository.save(jobSkill); // Lưu vào cơ sở dữ liệu
        }

    }
    public List<Candidate> findCandidatesMatchWithJob(Long jobId) {
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new EntityNotFoundException("Job not found"));
    List<Skill> skills = jobRepository.findSkillsByJobId(jobId);
    return candidateRepository.findCandidatesBySkillsAndLocation(skills, job.getCompany().getAddress().getCity());
}
}

