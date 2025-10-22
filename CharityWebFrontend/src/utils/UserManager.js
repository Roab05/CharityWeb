// Demo in-memory user manager. Replace with real backend in production.
const UserManager = {
  users: [
    {
      id: 1,
      name: "Nguyễn Văn A",
      email: "admin@gayquy.vn",
      phone: "0123456789",
      password: "123456",
      donations: [
        { projectName: "Xây dựng trường học vùng cao", amount: 1000000, date: "2024-01-15" },
        { projectName: "Bảo vệ động vật hoang dã", amount: 500000, date: "2024-01-10" },
        { projectName: "Hỗ trợ người già neo đơn", amount: 2000000, date: "2024-01-05" }
      ]
    },
    {
      id: 2,
      name: "Trần Thị B",
      email: "user@gayquy.vn",
      phone: "0987654321",
      password: "password",
      donations: [
        { projectName: "Trồng rừng xanh", amount: 500000, date: "2024-01-12" }
      ]
    }
  ],
  login(email, password) {
    const user = this.users.find(u => u.email === email && u.password === password);
    return user ? { ...user } : null;
  },
  register(userData) {
    const existing = this.users.find(u => u.email === userData.email);
    if (existing) return { success: false, message: "Email đã được sử dụng!" };
    const newUser = { id: this.users.length + 1, ...userData, donations: [] };
    this.users.push(newUser);
    return { success: true, user: { ...newUser } };
  },
  updateUser(userId, updatedData) {
    const idx = this.users.findIndex(u => u.id === userId);
    if (idx !== -1) {
      this.users[idx] = { ...this.users[idx], ...updatedData };
      return { ...this.users[idx] };
    }
    return null;
  },
  addDonation(userId, donation) {
    const u = this.users.find(x => x.id === userId);
    if (u) { u.donations.push(donation); return true; }
    return false;
  }
};

export default UserManager;
