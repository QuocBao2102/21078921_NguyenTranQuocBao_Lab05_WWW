package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.frontend.controllers;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.*;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.JobRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.CandidateServices;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.CompanyServices;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.InvitationService;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.JobServices;

import java.util.List;
@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobServices jobService;
    @Autowired
    private CandidateServices candidateService;
    @Autowired
    private CompanyServices companyService; // Service xử lý Company
    @Autowired
     CandidateRepository candidateRepository;
    @Autowired
    private JobRepository jobReponsitory;
    @Autowired
    private InvitationService invitationService;
    @GetMapping("/create")
    public String showCreateJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("companies", companyService.getAllCompanies()); // Gửi danh sách công ty
        return "jobs/create";
    }

    @PostMapping("/create")
    public String createJob(@RequestParam Long companyId, @ModelAttribute Job job) {
        Company company = companyService.getCompanyById(companyId); // Lấy Company từ ID
        job.setCompany(company); // Gắn công ty vào Job
        jobService.saveJob(job); // Lưu Job
        return "redirect:/company/job-list";
    }
    @GetMapping("/list")
    public String showJobList(Model model) {
        List<Job> jobs = jobService.getAllJobs(); // Gọi service để lấy danh sách công việc
        model.addAttribute("jobs", jobs);
        return "jobs/job-list"; // Tên của template để hiển thị danh sách công việc
    }
    @GetMapping("/search")
    public String searchJobs(@RequestParam("keyword") String keyword, Model model) {
        List<Job> jobs = jobService.searchJobsByKeyword(keyword);
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        return "home";
    }
    @GetMapping("/detail/{id}")
    public String findJobDetails(@PathVariable Long id, Model model) {
        // Lấy công việc theo ID
        Job job = jobService.getJobDetails(id);
        if (job == null) {
            return "error";  // Bạn có thể xử lý trường hợp không tìm thấy công việc
        }
        model.addAttribute("job", job);

        // Lấy kỹ năng của công việc
        List<Skill> skills = jobService.getJobSkills(id);
        model.addAttribute("skills", skills);

        // Tìm ứng viên có kỹ năng phù hợp
        List<Candidate> candidates = candidateService.getCandidatesBySkills(skills);
        model.addAttribute("candidates", candidates);
        return "jobs/detail-job";  // Chuyển đến giao diện chi tiết công việc
    }
    @GetMapping("/detail/{jobId}/candidates")
    public String showCandidatesForJob(@PathVariable Long jobId, Model model) {
        List<Candidate> candidates = jobService.findCandidatesMatchWithJob(jobId);
        model.addAttribute("candidates", candidates);
        return "jobs/candidates-list";
    }
    @GetMapping("/findJobWithcan")
    public ModelAndView findJobWithCan(@SessionAttribute(value = "candidateLogin", required = false) Candidate candidate) {
        if (candidate == null) {
            // Nếu ứng viên không đăng nhập, chuyển hướng đến trang login
            return new ModelAndView("redirect:/login");
        }

        // Lấy danh sách công việc phù hợp với ứng viên
        List<Job> listjobs = jobReponsitory.findAllJobMatchWithCandidate(candidate.getId());
        ModelAndView mav = new ModelAndView("jobs/jobWithCan");
        mav.addObject("jobs", listjobs);
        return mav;
    }
    // Endpoint để hiển thị chi tiết công việc
    @GetMapping("/details/{id}")
    public ModelAndView jobDetails(@PathVariable Long id) {
        // Tìm công việc theo ID
        Job job = jobReponsitory.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công việc với ID: " + id));

        ModelAndView mav = new ModelAndView("jobs/jobDetails");
        mav.addObject("job", job);
        return mav;
    }
    @PostMapping("/apply/{jobId}")
    public String applyForJob(@PathVariable Long jobId, @SessionAttribute("candidateLogin") Candidate candidate, Model model) {
        Job job = jobService.getJobDetails(jobId); // Lấy công việc theo ID
        if (job == null) {
            model.addAttribute("error", "Không tìm thấy công việc.");
            return "error"; // Chuyển đến trang lỗi nếu không tìm thấy công việc
        }

        try {
            // Gửi email thông báo ứng tuyển
            invitationService.sendApplicationEmail(job.getCompany().getEmail(), job.getName(), candidate.getFullName());
            model.addAttribute("message", "Đơn ứng tuyển đã được gửi thành công!");
        } catch (MessagingException e) {
            model.addAttribute("error", "Gửi email không thành công: " + e.getMessage());
        }
        return "redirect:/jobs/findJobWithcan"; // Chuyển hướng về danh sách công việc phù hợp
    }

}
