import axios from "axios";

const REST_API_BASE_URL = "http://localhost:8081/api/users"

export const getUserById = (userId) => axios.get(REST_API_BASE_URL + '/' + userId);

export const registerAccount = (newUser) => axios.post(REST_API_BASE_URL + '/register', newUser);

export const login = (loginDto) => axios.post(REST_API_BASE_URL + '/login', loginDto);

export const updateUserInfo = (data) => axios.put(REST_API_BASE_URL + '/info_update', data);

export const getUserTotalDonation = (id) => axios.get(REST_API_BASE_URL + '/' + id + '/total_donation');

export const updatePassword = (data) => axios.put(REST_API_BASE_URL + '/password_update', data);