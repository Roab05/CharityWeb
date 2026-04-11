package group3.project.charityweb.service;

import group3.project.charityweb.model.dto.request.LoginRequest;
import group3.project.charityweb.model.dto.request.RegisterIndivRequest;
import group3.project.charityweb.model.dto.request.RegisterOrgRequest;

import java.util.Map;

public interface AuthService {
    String registerIndividual(RegisterIndivRequest request);
    String registerOrganization(RegisterOrgRequest request);

    Map<String, Object> authenticate(LoginRequest loginRequest);

    String refreshAccessToken(String refreshToken);
}
