import axios from "axios";

const REST_API_BASE_URL = "http://localhost:8081/api/projects"

export const getProject = (projectId) => axios.get(REST_API_BASE_URL + '/' + projectId);

export const getAllProjects = () => axios.get(REST_API_BASE_URL);

export const uploadProject = (project) => axios.post(REST_API_BASE_URL + '/upload', project);

export const updateProjectAmount = (data) => axios.put(REST_API_BASE_URL + "/update_amount", data);

export const getProjectCurrentAmount = (id) => axios.get(REST_API_BASE_URL + "/" + id + "/current_amount");