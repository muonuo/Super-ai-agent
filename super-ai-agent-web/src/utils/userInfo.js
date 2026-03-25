/**
 * 用户信息管理工具
 * 用于生成和存储用户ID和用户名
 */

const USER_ID_KEY = "manus_user_id";
const USER_NAME_KEY = "manus_user_name";

/**
 * 生成唯一的用户ID
 */
function generateUserId() {
  return (
    "user_" + Date.now() + "_" + Math.random().toString(36).substring(2, 9)
  );
}

/**
 * 生成默认用户名
 */
function generateUserName() {
  const adjectives = [
    "聪明的",
    "勇敢的",
    "快乐的",
    "友善的",
    "睿智的",
    "活泼的",
    "温柔的",
    "坚强的",
  ];
  const nouns = [
    "小熊",
    "小兔",
    "小鸟",
    "小鹿",
    "小猫",
    "小狗",
    "小狐",
    "小鱼",
  ];
  const adj = adjectives[Math.floor(Math.random() * adjectives.length)];
  const noun = nouns[Math.floor(Math.random() * nouns.length)];
  return adj + noun;
}

/**
 * 获取或创建用户ID
 */
export function getUserId() {
  let userId = localStorage.getItem(USER_ID_KEY);
  if (!userId) {
    userId = generateUserId();
    localStorage.setItem(USER_ID_KEY, userId);
  }
  return userId;
}

/**
 * 获取或创建用户名
 */
export function getUserName() {
  let userName = localStorage.getItem(USER_NAME_KEY);
  if (!userName) {
    userName = generateUserName();
    localStorage.setItem(USER_NAME_KEY, userName);
  }
  return userName;
}

/**
 * 设置用户名
 */
export function setUserName(name) {
  localStorage.setItem(USER_NAME_KEY, name);
}

/**
 * 获取完整的用户信息
 */
export function getUserInfo() {
  return {
    userId: getUserId(),
    userName: getUserName(),
  };
}
