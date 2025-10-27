import axios from "axios";

const REST_API_BASE_URL = "http://localhost:8081/api/projects"

export const getProject = (id) => axios.get(REST_API_BASE_URL + '/' + id);

export const getAllProjects = () => axios.get(REST_API_BASE_URL);

export const uploadProject = (project) => axios.post(REST_API_BASE_URL + '/upload', project);

export const updateProjectAmount = (data) => axios.put(REST_API_BASE_URL + "/update_amount", data);

export const getProjectCurrentAmount = (id) => axios.get(REST_API_BASE_URL + "/" + id + "/current_amount");

export const getProjectDaysLeft = (id) => axios.get(REST_API_BASE_URL + "/" + id + "/days_left");

export const getProjectState = (id) => axios.get(REST_API_BASE_URL + "/" + id + "/state");