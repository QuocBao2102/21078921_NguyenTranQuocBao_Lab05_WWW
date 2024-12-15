package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.frontend.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Candidate;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Company;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Job;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.JobRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.*;

import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobServices jobServices;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private CompanyServices companyServices;
    @Autowired
    private CandidateServices candidateServices;

    @Autowired
    private EmailService emailServices;
    // Trang Dashboard cho công ty
    @GetMapping("/dashboard")
    public String showCompanyDashboard(Model model) {
        model.addAttribute("jobs", jobServices.getAllJobs());
        model.addAttribute("candidates", candidateServices.getAllCandidates());// Nếu cần thêm thông tin ứng viên
        return "company/dashboard"; // Giao diện Dashboard cho công ty
    }
    // Hiển thị form tạo công việc
    @GetMapping("/create-job")
    public String showCreateJobForm(Model model) {
        List<Company> companies = companyServices.getAllCompanies(); // Lấy danh sách công ty
        model.addAttribute("companies", companies); // Thêm danh sách công ty vào model
        model.addAttribute("job", new Job()); // Thêm đối tượng Job mới vào model
        return "/company/create-job"; // Trả về view
    }

    // Xử lý việc tạo công việc
    @PostMapping("/create-job")
    public String createJob(@ModelAttribute Job job, @RequestParam long companyId) {
        Company company = companyServices.getCompanyById(companyId); // Lấy Company từ ID
        job.setCompany(company);  // Gán công ty vào job
        jobServices.saveJob(job); // Lưu job vào cơ sở dữ liệu
        return "redirect:/company/job-list"; // Điều hướng đến trang danh sách công việc
    }
    // Trang danh sách công việc
    @GetMapping("/job-list")
    public String showJobList(Model model) {
        model.addAttribute("jobs", jobServices.getAllJobs());
        return "company/job-list"; // Giao diện danh sách công việc cho công ty
    }

    // Trang danh sách ứng viên
    @GetMapping("/candidate-list")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateServices.getAllCandidates());
        return "company/candidate-list"; // Giao diện danh sách ứng viên cho công ty
    }
    @PostMapping("/send-invitation")
    public String sendInvitation(@RequestParam("candidateEmail") String candidateEmail,
                                 @RequestParam("senderName") String senderName,
                                 @RequestParam("jobTitle") String jobTitle,
                                 @RequestParam("jobLink") String jobLink,
                                 Model model) {
        try {
            // Gửi email mời ứng viên
            emailServices.sendJobInvitationEmail(candidateEmail, senderName, jobTitle, jobLink);
            model.addAttribute("message", "Email mời ứng tuyển đã được gửi đến ứng viên.");
        } catch (MessagingException e) {
            model.addAttribute("message", "Có lỗi khi gửi email mời ứng tuyển.");
        }
        return "redirect:/company/candidate-list";
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login"; // Điều hướng đến trang login khi đăng xuất
    }
//    @PostMapping("/findCandiatewithJob")
//    public String findCandidatesWithJob(@RequestParam Long jobId, Model model) {
//        List<Candidate> candidates = candidateServices.findCandidatesByJobId(jobId);
//        model.addAttribute("candidates", candidates);
//        model.addAttribute("jobs", jobServices.getAllJobs()); // Để hiển thị lại danh sách công việc
//        return "company/candidate-list"; // Đường dẫn đến view danh sách ứng viên
//    }



}
