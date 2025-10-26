import axios from "axios";

const REST_API_BASE_URL = "http://localhost:8081/api/donors"

export const getDonor = (donorId) => axios.get(REST_API_BASE_URL + '/' + donorId);

export const registerAccount = (newDonor) => axios.post(REST_API_BASE_URL + '/register', newDonor);

export const login = (loginDto) => axios.post(REST_API_BASE_URL + '/login', loginDto);

export const updateDonorInfo = (data) => axios.put(REST_API_BASE_URL + '/info_update', data);

export const updateDonorTotalDonation = (data) => axios.put(REST_API_BASE_URL + '/donation_update', data);

export const updatePassword = (donorId, passwordUpdateDto) => axios.put(REST_API_BASE_URL + '/' + donorId + '/password_update', passwordUpdateDto);