package group3.project.charityweb.controller;

import group3.project.charityweb.dto.ProjectUpdateDto;
import group3.project.charityweb.model.Project;
import group3.project.charityweb.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAll();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Project> getProject(@PathVariable("id") String id){
        Project project = projectService.getById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<Project> uploadProject(@RequestBody Project project){
        Project newProject = projectService.upload(project);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    @PutMapping(path = "/update_amount")
    public ResponseEntity<Project> updateProjectAmount(@RequestBody ProjectUpdateDto projectUpdateDto) {
        Project updatedProject = projectService.updateAmount(projectUpdateDto);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/current_amount")
    public ResponseEntity<Long> getProjectCurrentAmount(@PathVariable("id") String id){
        Long currentAmount = projectService.getCurrentAmount(id);
        return new ResponseEntity<>(currentAmount, HttpStatus.OK);
    }
}
