import {useEffect, useState} from "react";
import {fetchWelcome} from "../services/fetchWelcome";
import {WelcomeData} from "../types/welcome/WelcomeInterfaces";
import {ParsedError} from "../types/errors/ParseErrorTypes";


export const useWelcomeData = () => {
    const [data, setData] = useState<WelcomeData | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadData = async () => {
            try {
                const welcomeData = await fetchWelcome();
                setData(welcomeData);
            } catch (err) {
                setError(err as ParsedError);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, []);

    return { data, errorWelcomeData: error, loadingWelcomeData: loading };
};