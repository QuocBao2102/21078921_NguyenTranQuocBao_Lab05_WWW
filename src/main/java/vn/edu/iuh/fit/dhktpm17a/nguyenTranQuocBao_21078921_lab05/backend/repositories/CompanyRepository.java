package vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.dhktpm17a.nguyenTranQuocBao_21078921_lab05.backend.models.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByCompanyLoginAndCompanyPassword(String companyLogin, String companyPassword);

}
