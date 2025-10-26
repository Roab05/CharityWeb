import axios from "axios";

const REST_API_BASE_URL = "http://localhost:8081/api/donations"

export const getAll = () => axios.get(REST_API_BASE_URL);

export const getByDonorId = (id) => axios.get(REST_API_BASE_URL + "/donors/" + id);

export const getByProjectId = (id) => axios.get(REST_API_BASE_URL + "/projects/" + id);

export const addDonation = (donation) => axios.post(REST_API_BASE_URL + "/add", donation);

