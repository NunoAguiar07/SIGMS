import {AccessRolesScreen} from "../../screens/AccessRolesScreen";
import ErrorHandler from "../(public)/error";
import {Alert} from "react-native";
import {processApproval} from "../../requests/authorized/ProcessApprovalRequest";
import {useCallback, useState} from "react";
import {getPendingApprovals} from "../../requests/authorized/PendingApprovalsRequest";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {AccessRoleInterface} from "../../interfaces/AccessRoleInterface";
import {useFocusEffect} from "expo-router";


const AccessRoles = () => {
    const [approvals, setApprovals] = useState<AccessRoleInterface[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [hasNext, setHasNext] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const LIMIT = 9;

    const fetchApprovals = async (page: number) => {
        setIsLoading(true);
        setError(null);

        const offset = page * LIMIT;
        const loadApprovals = getPendingApprovals(LIMIT, offset, setError);
        const data = await loadApprovals();
        console.log(data);
        if (data) {
            setApprovals(data);
            console.log(approvals);
            setHasNext(data.length === LIMIT);
        }

        setIsLoading(false);
    };

    useFocusEffect(
        useCallback(() => {
            fetchApprovals(currentPage);
        }, [currentPage]) // Add dependencies if needed
    );

    const handleApprove = async (id: number) => {
        // Find the approval to get its token
        const approval = approvals.find(a => a.id === id);
        if (!approval) return;

        const process = processApproval(approval.verificationToken, true, setError);
        const success = await process();

        if (success) {
            Alert.alert('Success', 'User has been approved');
            fetchApprovals(currentPage); // Refresh the list
        } else {
            Alert.alert('Error', 'Failed to approve user');
        }
    };

    const handleReject = async (id: number) => {
        const approval = approvals.find(a => a.id === id);
        if (!approval) return;

        const process = processApproval(approval.verificationToken, false, setError);
        const success = await process();

        if (success) {
            Alert.alert('Success', 'User has been rejected');
            fetchApprovals(currentPage); // Refresh the list
        } else {
            Alert.alert('Error', 'Failed to reject user');
        }
    };

    const handleNext = () => {
        setCurrentPage(prev => prev + 1);
    };

    const handlePrevious = () => {
        setCurrentPage(prev => Math.max(0, prev - 1));
    };

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <AccessRolesScreen
            approvals={approvals}
            onApprove={handleApprove}
            onReject={handleReject}
            onNext={handleNext}
            onPrevious={handlePrevious}
            currentPage={currentPage}
            hasNext={hasNext}
            isLoading={isLoading}
        />
    );
};

export default AccessRoles;