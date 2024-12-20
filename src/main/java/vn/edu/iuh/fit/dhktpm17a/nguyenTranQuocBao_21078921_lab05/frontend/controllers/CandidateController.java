package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.frontend.controllers;


import com.neovisionaries.i18n.CountryCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.*;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.CandidateServices;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.CompanyServices;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.JobServices;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.SkillServices;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Controller
@RequestMapping("/candidates")
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateServices candidateServices;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private  JobServices jobService;
    @Autowired
    private CompanyServices companyServices;
    @Autowired
    private SkillServices skillService;

    @GetMapping("/list")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/list_no_paging";
    }

    @GetMapping("")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        /*Page<Candidate> candidatePage= candidateServices.findPaginated(
                PageRequest.of(currentPage - 1, pageSize)
        );*/
        Page<Candidate> candidatePage = candidateServices.findAll(currentPage - 1,
                pageSize, "id", "asc");

        model.addAttribute("candidatePage", candidatePage);

        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "candidates/list";
    }

    @GetMapping("/show-add-form")
    public ModelAndView add(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        Candidate candidate = new Candidate();
        candidate.setAddress(new Address());
        modelAndView.addObject("candidate", candidate);
        modelAndView.addObject("address", candidate.getAddress());
        modelAndView.addObject("countries", CountryCode.values());
        modelAndView.setViewName("candidates/add");
        return modelAndView;
    }

    @PostMapping("/add")
    public String addCandidate(
            @ModelAttribute("candidate") Candidate candidate,
            @ModelAttribute("address") Address address,
            BindingResult result, Model model) {
        addressRepository.save(address);
        candidate.setAddress(address);
        candidateRepository.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/show-edit-form/{id}")
    public ModelAndView edit(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Candidate> opt = candidateRepository.findById(id);
        if (opt.isPresent()) {
            Candidate candidate = opt.get();
            modelAndView.addObject("candidate", candidate);
            modelAndView.addObject("address", candidate.getAddress());
            modelAndView.addObject("countries", CountryCode.values());
            modelAndView.setViewName("candidates/update");
        }
        return modelAndView;
    }

    @PostMapping("/update")
    public String update(
            @ModelAttribute("candidate") Candidate candidate,
            @ModelAttribute("address") Address address,
            BindingResult result, Model model) {
        addressRepository.save(address);
//        candidate.setAddress(address);
        candidateRepository.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable("id") long id
    ) {
        Optional<Candidate> opt = candidateRepository.findById(id);
        if (opt.isPresent()) {
            Candidate candidate = opt.get();
            candidate.setStatus(-1);
            candidateRepository.save(candidate);
        }
        return "redirect:/candidates";
    }
    @GetMapping("/candidates/by-skill/{skillId}")
    public String showCandidatesBySkill(@PathVariable Long skillId, Model model) {
        // Lấy danh sách ứng viên có kỹ năng tương ứng với skillId
        List<Candidate> candidates = candidateServices.getCandidatesBySkillId(skillId);

        model.addAttribute("skillId", skillId);  // Thêm skillId vào mô hình để hiển thị
        model.addAttribute("candidates", candidates);  // Thêm danh sách ứng viên vào mô hình
        return "company/candidate-list"; // Trả về view danh sách ứng viên
    }
@GetMapping("/candidates/by-skill")
public String showCandidatesBySkill(@RequestParam("skillName") String skillName, Model model) {
    try {
        // Tìm kỹ năng theo tên
        Skill skill = skillService.findByName(skillName);

        // Lấy danh sách ứng viên có kỹ năng này
        List<Candidate> candidates = candidateServices.getCandidatesBySkill(skill.getId());

        // Đưa dữ liệu vào model
        model.addAttribute("candidates", candidates);
        model.addAttribute("skillName", skillName);

        return "company/candidate-list"; // Trả về view danh sách ứng viên
    } catch (EntityNotFoundException ex) {
        model.addAttribute("message", ex.getMessage());
        return "company/candidate-list"; // Nếu không tìm thấy kỹ năng, trả về lỗi
    }
}

    @GetMapping("/create")
    public String showCreateCandidateForm(Model model) {
        model.addAttribute("candidate", new Candidate());
        return "candidates/create";
    }


    @PostMapping("/create")
    public String createCandidate(@ModelAttribute Candidate candidate) {
        candidateServices.saveCandidate(candidate);
        return "redirect:/candidates/list";
    }
    @GetMapping("/home")
    public String showJobListings(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "candidates/home";  // Giao diện hiển thị danh sách công việc
    }
    // Hiển thị Dashboard cho ứng viên
    @GetMapping("/dashboard")
    public String showCandidateDashboard(Model model) {
        model.addAttribute("jobs", jobService.getAllJobs());
        return "candidates/dashboard"; // Giao diện Dashboard cho ứng viên
    }
    // Xem danh sách công việc
    @GetMapping("/job-list")
    public String showJobList(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "candidates/job-list"; // Trang hiển thị danh sách công việc
    }
    // Tìm kiếm công việc
    @GetMapping("/search")
    public String searchJobs(@RequestParam String keyword, Model model) {
        List<Job> jobs = jobService.searchJobs(keyword);
        model.addAttribute("jobs", jobs);
        return "candidates/job-list"; // Trang kết quả tìm kiếm công việc cho ứng viên
    }
    // Đăng xuất
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }


}