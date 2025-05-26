import axios from 'axios';

export const handleAxiosError = (error: any, setError: (error: any) => void) => {
    if (axios.isAxiosError(error)) {
        if (error.response) {
            const parsedError = {
                status: error.response.status,
                message: error.response.data?.detail || 'Request failed',
            };
            setError(parsedError);
        } else if (error.request) {
            setError({ message: 'No response received from server' });
        } else {
            setError({ message: error.message });
        }
    } else {
        setError({ message: 'An unexpected error occurred' });
    }
};