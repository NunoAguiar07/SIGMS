import React, {useEffect, useState} from 'react';
import {RegisterScreen} from '../../../screens/RegisterScreen';
import {RegisterRequest} from '../../../requests/auth/RegisterRequest';
import {ErrorInterface} from '../../../interfaces/ErrorInterface';
import {useRouter} from 'expo-router';
import ErrorHandler from "../error";
import {UniversityInterface} from "../../../interfaces/UniversityInterface";
import {useDebounce} from "use-debounce";
import {UniversitiesRequest} from "../../../requests/UniversityRequest";

const Register = () => {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('STUDENT');
    const [universityId, setUniversityId] = useState<number>(0);
    const [universities, setUniversities] = useState<UniversityInterface[]>([]);
    const [selectedUniversity, setSelectedUniversity] = useState<UniversityInterface | null>(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [skipSearch, setSkipSearch] = useState(false);
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const router = useRouter();

    useEffect(() => {
        if (skipSearch) {
            setSkipSearch(false);
            return;
        }
        if (debouncedSearchQuery.trim().length > 0) {
            const loadUniversities = UniversitiesRequest(debouncedSearchQuery, setUniversities, setError);
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
        const register = RegisterRequest(email, username, password, role.toUpperCase(), universityId, setError);
        const resultMessage = await register();
        if (resultMessage) {
            alert(resultMessage);
        }
    };

    const handleNavigateToLogin = () => {
        router.push('/auth/login');
    };

    const handleRoleChange = (itemValue: string) => {
        setRole(itemValue);
    };

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <RegisterScreen
            email={email}
            username={username}
            password={password}
            role={role}
            universityId={universityId}
            universities={universities}
            selectedUniversity={selectedUniversity}
            searchQuery={searchQuery}
            onEmailChange={setEmail}
            onUsernameChange={setUsername}
            onPasswordChange={setPassword}
            onRoleChange={handleRoleChange}
            onUniversityChange={setUniversityId}
            onSearchChange={setSearchQuery}
            onUniversitySelect={handleUniversitySelect}
            onRegister={handleRegister}
            onNavigateToLogin={handleNavigateToLogin}
        />
    );
};

export default Register;