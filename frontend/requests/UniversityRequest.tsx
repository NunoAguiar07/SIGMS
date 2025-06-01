import axios from "axios";

export const UniversitiesRequest = (searchQuery: string, setUniversities: any, setError: any) => {
    return async () => {
        try {
            const apiUrl = process.env.EXPO_PUBLIC_API_URL
            const response = await axios.get(
                `${apiUrl}universities?search=${encodeURIComponent(searchQuery)}`,
                {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );
            if (response.status === 200) {
                setUniversities(response.data.data);
            }
        } catch (error) {
            setError(error);
        }
    };
};