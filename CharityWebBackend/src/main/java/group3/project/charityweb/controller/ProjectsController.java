package group3.project.charityweb.controller;

import group3.project.charityweb.dto.ProjectUpdateDto;
import group3.project.charityweb.model.Projects;
import group3.project.charityweb.service.ProjectsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/projects")
public class ProjectsController {

    private final ProjectsService projectsService;

    @GetMapping
    public ResponseEntity<List<Projects>> getAllProjects() {
        List<Projects> projects = projectsService.getAll();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Projects> getProject(@PathVariable("id") String id){
        Projects project = projectsService.getById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<Projects> uploadProject(@RequestBody Projects project){
        Projects newProject = projectsService.upload(project);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    @PutMapping(path = "/update_amount")
    public ResponseEntity<Projects> updateProjectAmount(@RequestBody ProjectUpdateDto projectUpdateDto) {
        Projects updatedProject = projectsService.updateAmount(projectUpdateDto);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/current_amount")
    public ResponseEntity<Long> getProjectCurrentAmount(@PathVariable("id") String id){
        Long currentAmount = projectsService.getCurrentAmount(id);
        return new ResponseEntity<>(currentAmount, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/donation_count")
    public ResponseEntity<Long> getProjectDonationCount(@PathVariable("id") String id){
        Long donationCount = projectsService.getDonationCount(id);
        return new ResponseEntity<>(donationCount, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/days_left")
    public ResponseEntity<Long> getProjectDaysLeft(@PathVariable("id") String id) {
        Long daysLeft = projectsService.getDaysLeft(id);
        return ResponseEntity.ok(daysLeft);
    }

    @GetMapping(path = "/{id}/state")
    public ResponseEntity<Integer> getProjectState(@PathVariable("id") String id) {
        Integer state = projectsService.setAndGetState(id);
        return ResponseEntity.ok(state);
    }

    @PutMapping(path = "/update_info")
    public ResponseEntity<Projects> updateProjectInfo(@RequestBody ProjectUpdateDto projectUpdateDto) {
        Projects updatedProject = projectsService.updateInfo(projectUpdateDto);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}/delete")
    public ResponseEntity<String> deleteProject(@PathVariable("id") String id) {
        projectsService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
