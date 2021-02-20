package project.lab_management_syst.persistence.repo;

import org.springframework.data.repository.CrudRepository;
import project.lab_management_syst.persistence.model.LabFormat;

public interface LabFormatRepository extends CrudRepository<LabFormat, String> {
    LabFormat findByRepoNamingSchema(String repoName);
}
