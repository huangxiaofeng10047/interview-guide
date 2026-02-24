import { request } from './request';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserInfo {
  id: number;
  username: string;
  email: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfo;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

// 登录API
export const login = async (data: LoginRequest): Promise<LoginResponse> => {
  const response = await request.post<LoginResponse>('/api/auth/login', data);
  return response;
};

// 注册API
export const register = async (data: RegisterRequest): Promise<void> => {
  try {
    console.log('Sending register request with data:', data);
    const response = await request.post('/api/auth/register', data);
    console.log('Register request successful:', response);
  } catch (error) {
    console.error('Register request failed:', error);
    throw error;
  }
};

// 保存认证信息到本地存储
export const saveAuthInfo = (response: LoginResponse): void => {
  localStorage.setItem('authToken', response.token);
  localStorage.setItem('userInfo', JSON.stringify(response.user));
  localStorage.setItem('tokenType', response.tokenType);
  localStorage.setItem('expiresIn', response.expiresIn.toString());
  localStorage.setItem('loginTime', Date.now().toString());
};

// 从本地存储获取认证信息
export const getAuthInfo = (): {
  token: string | null;
  userInfo: UserInfo | null;
  tokenType: string | null;
} => {
  const token = localStorage.getItem('authToken');
  const userInfoStr = localStorage.getItem('userInfo');
  const tokenType = localStorage.getItem('tokenType');

  let userInfo = null;
  if (userInfoStr) {
    try {
      userInfo = JSON.parse(userInfoStr);
    } catch (error) {
      console.error('Failed to parse user info:', error);
    }
  }

  return {
    token,
    userInfo,
    tokenType
  };
};

// 清除认证信息
export const clearAuthInfo = (): void => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('tokenType');
  localStorage.removeItem('expiresIn');
  localStorage.removeItem('loginTime');
};

// 检查是否已登录
export const isAuthenticated = (): boolean => {
  const { token } = getAuthInfo();
  return !!token;
};
