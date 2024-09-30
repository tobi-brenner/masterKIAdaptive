import { BE_BASE_URL } from '@/constants';

interface FetchOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  headers?: HeadersInit;
  body?: any;
}



export async function fetchFromAPI<T>(
    endpoint: string,
    options: FetchOptions = {},
): Promise<T> {
  const { body, ...customConfig } = options;
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...customConfig.headers,
  };

  const config: RequestInit = {
    method: body ? 'POST' : 'GET',
    ...customConfig,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  };


  try {
    const response = await fetch(`${BE_BASE_URL}${endpoint}`, config);

    if (!response.ok) {
      // Attempt to read response as JSON first, fall back to text
      let errorMessage = 'Something went wrong';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
        console.error('Error data:', errorData);
      } catch (jsonError) {
        errorMessage = await response.text();
        console.error('Error response from backend:', errorMessage);
      }
      // throw new Error(errorMessage);
    }

    // Handle empty response (e.g., from DELETE requests)
    if (response.status === 204 || response.status === 205 || response.headers.get('Content-Length') === '0') {
      return {} as T;
    }

    // Assumes the API returns JSON. Adjust as needed.
    const res = await response.json();
    // console.log('res', res);
    return res as T;
  } catch (error) {
    console.error('API call failed:', error);
    throw new Error('Failed to fetch data from API.');
  }
}


interface UploadOptions {
  method?: 'POST';
  headers?: HeadersInit;
  body: FormData;
}

// Utility function to upload files to an API
export async function uploadFilesToAPI<T>(
  endpoint: string,
  options: UploadOptions,
): Promise<T> {
  const { body, ...customConfig } = options;
  console.log("endpoint",endpoint);
  console.log("endpoint1", `${BE_BASE_URL}${endpoint}`);

  const config: RequestInit = {
    method: 'POST',
    ...customConfig,
    body,
  };

  try {
    const response = await fetch(`${BE_BASE_URL}${endpoint}`, config);
    if (!response.ok) {
      // This assumes your API returns error data in JSON format
      const errorData = await response.json();
      throw new Error(errorData.message || 'Something went wrong');
    }

    // Assumes the API returns JSON. Adjust as needed.
    return (await response.json()) as T;
  } catch (error) {
    console.error('API call failed:', error);
    throw new Error('Failed to upload files to API.');
  }
}
