//package group3.project.charityweb.service.impl;
//
//import group3.project.charityweb.exception.UnauthorizedAccessException;
//import group3.project.charityweb.model.dto.response.ProjectResponse;
//import group3.project.charityweb.model.entity.Organization;
//import group3.project.charityweb.model.entity.Project;
//import group3.project.charityweb.model.entity.ProjectCategory;
//import group3.project.charityweb.model.enums.ProjectStatus;
//import group3.project.charityweb.repository.OrganizationRepository;
//import group3.project.charityweb.repository.ProjectCategoryRepository;
//import group3.project.charityweb.repository.ProjectRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ProjectServiceImplTest {
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Mock
//    private OrganizationRepository organizationRepository;
//
//    @Mock
//    private ProjectCategoryRepository categoryRepository;
//
//    private ProjectServiceImpl projectService;
//
//    @BeforeEach
//    void setUp() {
//        projectService = new ProjectServiceImpl(projectRepository, organizationRepository, categoryRepository);
//    }
//
//    @Test
//    void getMyManagedProjects_shouldReturnProjectsForOrganization() {
//        String username = "org1";
//        Organization organization = new Organization();
//        organization.setUsername(username);
//        organization.setName("Org One");
//
//        ProjectCategory category = new ProjectCategory();
//        category.setCategoryName("Health");
//
//        Project project = new Project();
//        project.setProjectId("p1");
//        project.setProjectName("Project One");
//        project.setCreatedAt(LocalDateTime.now());
//        project.setStatus(ProjectStatus.ACTIVE);
//        project.setCategories(List.of(category));
//        project.setOrganizations(List.of(organization));
//
//        when(organizationRepository.findByUsername(username)).thenReturn(Optional.of(organization));
//        when(projectRepository.findByOrganizations_UsernameAndStatusOrderByCreatedAtDesc(
//                eq(username), eq(ProjectStatus.ACTIVE), any(PageRequest.class)))
//                .thenReturn(new PageImpl<>(List.of(project)));
//
//        Page<ProjectResponse> result = projectService.getMyManagedProjects(username, ProjectStatus.ACTIVE, 0, 10);
//
//        assertEquals(1, result.getTotalElements());
//        assertEquals("p1", result.getContent().get(0).getProjectId());
//        assertEquals("Org One", result.getContent().get(0).getOrganizationNames().get(0));
//        assertEquals("Health", result.getContent().get(0).getCategories().get(0));
//    }
//
//    @Test
//    void getMyManagedProjects_shouldUseNoStatusQueryWhenStatusIsNull() {
//        String username = "org1";
//        Organization organization = new Organization();
//        organization.setUsername(username);
//
//        when(organizationRepository.findByUsername(username)).thenReturn(Optional.of(organization));
//        when(projectRepository.findByOrganizations_UsernameOrderByCreatedAtDesc(eq(username), any(PageRequest.class)))
//                .thenReturn(Page.empty());
//
//        projectService.getMyManagedProjects(username, null, 0, 10);
//
//        verify(projectRepository).findByOrganizations_UsernameOrderByCreatedAtDesc(eq(username), any(PageRequest.class));
//        verify(projectRepository, never()).findByOrganizations_UsernameAndStatusOrderByCreatedAtDesc(any(), any(), any());
//    }
//
//    @Test
//    void getMyManagedProjects_shouldThrowWhenUserIsNotOrganization() {
//        String username = "individual1";
//        when(organizationRepository.findByUsername(username)).thenReturn(Optional.empty());
//
//        assertThrows(UnauthorizedAccessException.class,
//                () -> projectService.getMyManagedProjects(username, ProjectStatus.ACTIVE, 0, 10));
//
//        verify(projectRepository, never()).findByOrganizations_UsernameOrderByCreatedAtDesc(any(), any());
//        verify(projectRepository, never()).findByOrganizations_UsernameAndStatusOrderByCreatedAtDesc(any(), any(), any());
//    }
//}
//
