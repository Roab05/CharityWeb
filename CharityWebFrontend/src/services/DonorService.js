import axios from "axios";

const REST_API_BASE_URL = "http://localhost:8081/api/donors"

export const getDonor = (donorId) => axios.get(REST_API_BASE_URL + '/' + donorId);

export const registerAccount = (newDonor) => axios.post(REST_API_BASE_URL + '/register', newDonor);

export const login = (loginDto) => axios.post(REST_API_BASE_URL + '/login', loginDto);

export const updateDonorInfo = (donorId, donor) => axios.put(REST_API_BASE_URL + '/' + donorId, donor);

export const updatePassword = (donorId, passwordUpdateDto) => axios.put(REST_API_BASE_URL + '/' + donorId, passwordUpdateDto);