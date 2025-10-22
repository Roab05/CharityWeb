const mockProjects = [
  {
    id: 1,
    name: "Xây dựng trường học vùng cao",
    image: "https://images.unsplash.com/photo-1497486751825-1233686d5d80?w=800&h=500&fit=crop",
    category: "education",
    description: "Xây dựng trường học cho trẻ em vùng cao Sapa, giúp các em có môi trường học tập tốt hơn.",
    targetAmount: 500000000,
    currentAmount: 320000000,
    supporters: 1250,
    daysLeft: 15,
    isActive: true
  },
  {
    id: 2,
    name: "Bảo vệ động vật hoang dã",
    image: "https://images.unsplash.com/photo-1564349683136-77e08dba1ef7?w=800&h=500&fit=crop",
    category: "wildlife",
    description: "Chương trình bảo vệ và chăm sóc các loài động vật hoang dã bị đe dọa tại Việt Nam.",
    targetAmount: 300000000,
    currentAmount: 180000000,
    supporters: 890,
    daysLeft: 22,
    isActive: true
  },
  {
    id: 3,
    name: "Hỗ trợ người già neo đơn",
    image: "https://images.unsplash.com/photo-1559027615-cd4628902d4a?w=800&h=500&fit=crop",
    category: "social",
    description: "Chăm sóc và hỗ trợ cuộc sống cho những người già neo đơn tại các tỉnh miền Trung.",
    targetAmount: 200000000,
    currentAmount: 200000000,
    supporters: 2100,
    daysLeft: 0,
    isActive: false
  },
  {
    id: 4,
    name: "Trồng rừng xanh",
    image: "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&h=500&fit=crop",
    category: "environment",
    description: "Trồng 10,000 cây xanh để phục hồi rừng và bảo vệ môi trường.",
    targetAmount: 150000000,
    currentAmount: 95000000,
    supporters: 650,
    daysLeft: 30,
    isActive: true
  }
];

export default mockProjects;
