import {useEffect, useState} from "react";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {Alert} from "react-native";
import {AdminEntityCreationScreen} from "../../screens/GenerateEntitiesScreen";
import {CreateSubjectRequest} from "../../requests/authorized/CreateSubjectRequest";
import {CreateClassRequest} from "../../requests/authorized/CreateClassRequest";
import {CreateRoomRequest} from "../../requests/authorized/CreateRoomSubject";
import {SubjectInterface} from "../../interfaces/SubjectInterface";
import {useDebounce} from "use-debounce";
import {SubjectsRequest} from "../../requests/authorized/SubjectsRequest";
import ErrorHandler from "../(public)/error";


const AdminEntityCreation = () => {
    const [selectedEntity, setSelectedEntity] = useState<'subject' | 'class' | 'room' | null>(null);
    const [formValues, setFormValues] = useState<any>({});
    const [error, setError] = useState<ErrorInterface | null>(null);
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [isLoading, setIsLoading] = useState(false);

    // Load subjects when class tab is selected
    useEffect(() => {
        if (selectedEntity === 'class' && debouncedSearchQuery.trim().length > 0) {
            loadSubjects();
        }
    }, [selectedEntity, debouncedSearchQuery]);

    const loadSubjects = async () => {
        setIsLoading(true);
        try {
            const fetchSubjects = SubjectsRequest(debouncedSearchQuery, setSubjects, setError);
            await fetchSubjects();
        } finally {
            setIsLoading(false);
        }
    };

    const handleSubmit = async () => {
        let response: any = false;

        if (selectedEntity === 'subject') {
            response = CreateSubjectRequest(formValues.name, setError);

        } else if (selectedEntity === 'class') {
            response = CreateClassRequest(
                formValues.name,
                formValues.subjectId,
                setError
            );
        } else if (selectedEntity === 'room') {
            response = CreateRoomRequest(
                formValues.name,
                formValues.capacity,
                formValues.type,
                setError
            );
        }

        const success = await response();
        if (success) {
            Alert.alert('Success', `Successfully created ${selectedEntity}`);
            setFormValues({});
            setSearchQuery('');
            setSubjects([]);
            setSelectedEntity(null);
        } else {
            Alert.alert('Error', `Failed to create ${selectedEntity}`);
        }
    };

    // Handle errors
    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <AdminEntityCreationScreen
            selectedEntity={selectedEntity}
            onEntitySelect={setSelectedEntity}
            formValues={formValues}
            setFormValues={setFormValues}
            onSubmit={handleSubmit}
            subjects={subjects}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            isLoading={isLoading}
        />
    );
};

export default AdminEntityCreation;