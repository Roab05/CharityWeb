package group3.project.charityweb.controller;

import group3.project.charityweb.model.Project;
import group3.project.charityweb.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Project> getProject(@PathVariable("id") String id){
        Project project = projectService.getById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<Project> upload(@RequestBody Project project){
        Project newProject = projectService.upload(project);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }
}
