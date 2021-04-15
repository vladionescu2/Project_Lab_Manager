package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.LabFormat;
import project.lab_management_syst.persistence.model.StudentRepo;

import java.util.List;

public interface StudentRepoRepository extends CrudRepository<StudentRepo, String> {
    public StudentRepo findByRepoName(String repoName);

    public List<StudentRepo> findByLab(LabFormat labFormat);
}
