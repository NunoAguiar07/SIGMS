import {AccessRoleInterface} from "../../types/AccessRoleInterface";


export interface AccessRoleScreenType {
    approvals: AccessRoleInterface[];
    currentPage: number;
    hasNext: boolean;
    onApprove: (id: number) => void;
    onReject: (id: number) => void;
    onNext: () => void;
    onPrevious: () => void;
}