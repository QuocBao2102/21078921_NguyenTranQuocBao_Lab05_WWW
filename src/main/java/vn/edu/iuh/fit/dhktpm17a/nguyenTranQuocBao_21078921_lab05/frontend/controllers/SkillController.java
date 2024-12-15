package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.frontend.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.enums.SkillType;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Candidate;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Company;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Skill;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories.SkillRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.EmailService;
//import vn.edu.iuh.fit.dhktpm17a.aoconghieu_21026511_lab05.backend.services.EmailService;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.services.SkillServices;

import java.util.List;

@Controller
@RequestMapping("/skills")
public class SkillController {
    @Autowired
    private SkillServices skillService;

    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private EmailService emailService;
    @GetMapping("/list")
    public String showSkillList(Model model) {
        model.addAttribute("skills", skillService.getSkillsByType(SkillType.TECHNICAL_SKILL));
        return "list-all";
    }

    @GetMapping("/create")
    public String showCreateSkillForm(Model model) {
        model.addAttribute("skill", new Skill());
        model.addAttribute("skillTypes", SkillType.values());
        return "skills/create";
    }

    @PostMapping("/create")
    public String createSkill(@ModelAttribute Skill skill) {
        skillService.saveSkill(skill);
        return "redirect:/skills/list-all";
    }

    @GetMapping("/list-all")
    public String showAllSkills(Model model) {
        model.addAttribute("skills", skillService.getAllSkills());
        return "skills/list-all";
    }
    @GetMapping("/viewSkills")
    public ModelAndView viewSkills(@SessionAttribute(value = "candidateLogin", required = false) Candidate candidate) {
        if (candidate == null) {
            // Nếu ứng viên không đăng nhập, chuyển hướng đến trang login
            return new ModelAndView("redirect:/login");
        }

        // Lấy danh sách kỹ năng của ứng viên từ service
        List<Skill> skills = skillService.getSkillsByUserId(candidate.getId());

        // Tạo ModelAndView và thêm kỹ năng vào
        ModelAndView mav = new ModelAndView("skills/viewSkills");
        mav.addObject("skills", skills);
        return mav;
    }

    @GetMapping("/learnSkills")
    public ModelAndView findSkillLearn(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        Candidate can=(Candidate) request.getSession().getAttribute("candidateLogin");
        List<Skill> skills= skillRepository.findSkillCandidateShouldLearn(can.getId());
        modelAndView.addObject("skills", skills);
        modelAndView.setViewName("skills/LearnSkills");
        return modelAndView;
    }
//    // Lấy danh sách kỹ năng cần học của ứng viên
//    @GetMapping("/findSkillLearn")
//    public ModelAndView findSkillLearn(HttpServletRequest request) {
//        Candidate candidate = (Candidate) request.getSession().getAttribute("candidateLogin");
//        if (candidate == null) {
//            return new ModelAndView("redirect:/login");
//        }
//
//        List<Skill> skills = skillRepository.findSkillCandidateShouldLearn(candidate.getId());
//        ModelAndView modelAndView = new ModelAndView("skills/KillsLearn");
//        modelAndView.addObject("skills", skills);
//        return modelAndView;
//    }
//
//    // Lấy danh sách kỹ năng của công ty và ứng viên phù hợp
//    @GetMapping("/findCadidateskillMatchOfCompany")
//    public ModelAndView findCandidateSkillMatchOfCompany(HttpServletRequest request) {
//        Company company = (Company) request.getSession().getAttribute("companyLogin");
//        if (company == null) {
//            return new ModelAndView("redirect:/login");
//        }
//
//        List<Skill> skills = skillRepository.findSkillByCompany(company.getId());
//        List<Candidate> candidates = candidateRepository.findAll().subList(0, 10);
//
//        ModelAndView mav = new ModelAndView("company/findCandidateSkillOfCompany");
//        mav.addObject("skills", skills);
//        mav.addObject("candidates", candidates);
//        return mav;
//    }
//
//    // Lọc ứng viên theo kỹ năng
//    @PostMapping("/findCandidatewithskill")
//    public ModelAndView findCandidateMatchWithSkill(@RequestParam("skillId") Long skillId, HttpServletRequest request) {
//        Company company = (Company) request.getSession().getAttribute("companyLogin");
//        if (company == null) {
//            return new ModelAndView("redirect:/login");
//        }
//
//        List<Candidate> candidates = candidateRepository.findCandidatesMatchWithSkills(skillId);
//        List<Skill> skills = skillRepository.findSkillByCompany(company.getId());
//
//        ModelAndView mav = new ModelAndView("company/findCandidateSkillOfCompany");
//        mav.addObject("skills", skills);
//        mav.addObject("candidates", candidates);
//        return mav;
//    }
//
//    // Gửi email mời ứng tuyển đến ứng viên
//    @PostMapping("/sendmail")
//    public ModelAndView sendMailToCandidate(@RequestParam("id") Long candidateId, HttpServletRequest request) {
//
//        Company company = (Company) request.getSession().getAttribute("companyLogin");
//        if (company == null) {
//            return new ModelAndView("redirect:/login");
//        }
//
//        List<Skill> skills = skillRepository.findSkillByCompany(company.getId());
//        List<Candidate> candidates = candidateRepository.findAll().subList(0, 10);
//
//        ModelAndView mav = new ModelAndView("company/findCandidateSkillOfCompany");
//        mav.addObject("skills", skills);
//        mav.addObject("candidates", candidates);
//
//        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
//        if (candidate != null) {
//            try {
//                invitationService.sendInvitation(candidate.getEmail(), candidate.getFullName());
//                mav.addObject("mess", "Gửi email mời ứng tuyển thành công!");
//            } catch (MessagingException e) {
//                mav.addObject("mess", "Lỗi khi gửi email. Vui lòng thử lại.");
//            }
//        } else {
//            mav.addObject("mess", "Không tìm thấy ứng viên!");
//        }
//
//        return mav;
//    }
}
