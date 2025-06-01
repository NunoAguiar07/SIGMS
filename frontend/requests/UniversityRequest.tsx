import axios from "axios";
import {apiUrl} from "./WelcomeRequest";

export const UniversitiesRequest = (searchQuery: string, setUniversities: any, setError: any) => {
    return async () => {
        try {
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