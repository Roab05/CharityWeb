package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.LoginRequest;
import group3.project.charityweb.model.dto.request.RegisterIndivRequest;
import group3.project.charityweb.model.dto.request.RegisterOrgRequest;

public interface AuthService {
    String registerIndividual(RegisterIndivRequest request);
    String registerOrganization(RegisterOrgRequest request);
    String authenticate(LoginRequest loginRequest);
}
