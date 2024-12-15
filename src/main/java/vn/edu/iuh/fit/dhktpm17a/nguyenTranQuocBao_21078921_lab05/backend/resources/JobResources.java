package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Candidate;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Job;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.JobServices;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobResources {
    @Autowired
    private JobServices jobService;

    @PostMapping
    public void saveJob(@RequestBody Job job) {
        jobService.saveJob(job);
    }

    @GetMapping("/by-skill/{skillId}")
    public List<Job> getJobsBySkill(@PathVariable Long skillId) {
        return jobService.getJobsBySkill(skillId);
    }
    @GetMapping("/search")
    public List<Job> searchJobs(@RequestParam("keyword") String keyword) {
        return jobService.searchJobsByKeyword(keyword);
    }
    // Lấy chi tiết công việc theo ID
    @GetMapping("/detail/{jobId}")
    public Job getJobDetails(@PathVariable Long jobId) {
        return jobService.getJobDetails(jobId);
    }
    @GetMapping("/detail/{jobId}/candidates")
public List<Candidate> getCandidatesForJob(@PathVariable Long jobId) {
    return jobService.findCandidatesMatchWithJob(jobId);
}
}