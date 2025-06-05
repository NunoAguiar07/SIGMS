import {clearMobileStorage, clearWebStorage} from "../utils/ClearCredentials";
import {useEffect, useState} from "react";
import {useLocalSearchParams, useRouter} from "expo-router";
import {getDeviceType} from "../utils/DeviceType";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {requestLogin} from "../services/auth/requestLogin";
import {useDebounce} from "use-debounce";
import {UniversityInterface} from "../types/UniversityInterface";
import {fetchUniversities} from "../services/fetchUniversities";
import {requestRegister} from "../services/auth/requestRegister";
import {requestVerifyAccount} from "../services/auth/requestVerifyAccount";
import {fetchUserInfo} from "../services/authorized/fetchUserInfo";

export const useLogout = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<ParsedError | null>(null);
    const router = useRouter();

    const handleLogout = async () => {
        setLoading(true);
        try {
            const deviceType = getDeviceType();
            if (deviceType !== 'WEB') {
                await clearMobileStorage();
            } else {
                await clearWebStorage();
            }
            router.replace('/welcome');
        } catch (error) {
            const parsedError : ParsedError = {
                message: 'Logout failed',
                status: 500
            }
            setError(parsedError as ParsedError);
        } finally {
            setLoading(false);
        }
    };

    return { handleLogout, loadingLogout: loading, errorLogout: error };
};

export const useLogin = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(false);
    const router = useRouter();

    const handleLogin = async () => {
        setLoading(true);
        setError(null);

        try {
            const deviceType = getDeviceType();
            const loginResult = await requestLogin(email, password, deviceType);
            if (loginResult.success) {
                await fetchUserInfo();
                setLoading(false);
                router.push('/home');
            }
        } catch (error) {
            setLoading(false);
            setError(error as ParsedError);
        }
    };

    const handleNavigateToRegister = () => {
        router.push('/auth/register');
    };

    return {
        email,
        setEmail,
        password,
        setPassword,
        error,
        loading,
        handleLogin,
        handleNavigateToRegister,
    };
};

export const useRegister = () => {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('STUDENT');
    const [universityId, setUniversityId] = useState(0);
    const [universities, setUniversities] = useState<UniversityInterface[]>([]);
    const [selectedUniversity, setSelectedUniversity] = useState<UniversityInterface | null>(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [skipSearch, setSkipSearch] = useState(false);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(false);
    const router = useRouter();

    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);

    useEffect(() => {
        if (skipSearch) {
            setSkipSearch(false);
            return;
        }
        if (debouncedSearchQuery.trim().length > 0) {
            const loadUniversities = async () => {
                try {
                    const results = await fetchUniversities(debouncedSearchQuery);
                    setUniversities(results);
                } catch (error) {
                    setError(error as ParsedError);
                }
            };
            loadUniversities();
        } else {
            setUniversities([]);
        }
    }, [debouncedSearchQuery]);

    const handleUniversitySelect = (university: UniversityInterface) => {
        setSelectedUniversity(university);
        setUniversityId(university.id);
        setSkipSearch(true);
        setSearchQuery(university.name);
        setUniversities([]);
    };

    const handleRegister = async () => {
        setLoading(true);
        setError(null);
        try {
            const resultMessage = await requestRegister(email, username, password, role.toUpperCase(), universityId);
            if (resultMessage) {
                alert(resultMessage);
                router.push('/auth/login');
            }
        } catch (error) {
            setError(error as ParsedError);
        } finally {
            setLoading(false);
        }
    };

    const handleNavigateToLogin = () => {
        router.push('/auth/login');
    };

    const handleRoleChange = (itemValue: string) => {
        setRole(itemValue);
    };

    return {
        email,
        setEmail,
        username,
        setUsername,
        password,
        setPassword,
        role,
        handleRoleChange,
        universities,
        searchQuery,
        setSearchQuery,
        error,
        loading,
        handleUniversitySelect,
        handleRegister,
        handleNavigateToLogin,
    };
};

export const useVerifyAccount = () => {
    const [verificationSuccess, setVerificationSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<ParsedError | null>(null);
    const router = useRouter();
    const params = useLocalSearchParams();
    const { token } = params;

    useEffect(() => {
        const verifyAccount = async () => {
            if (typeof token !== 'string') return;

            setLoading(true);
            setError(null);
            try {
                const success = await requestVerifyAccount(token);
                setVerificationSuccess(success);
            } catch (err) {
                setError(err as ParsedError);
            } finally {
                setLoading(false);
            }
        };

        verifyAccount();
    }, [token]);

    const handleNavigateToLogin = () => {
        router.replace('/auth/login');
    };

    return {
        verificationSuccess,
        loading,
        error,
        handleNavigateToLogin
    };
};