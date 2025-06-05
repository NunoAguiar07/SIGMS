
export interface ErrorScreenType {
    errorStatus: number;
    errorMessage: string;
    goBack: () => void;
}

export interface ErrorHandlerType {
    errorStatus: number;
    errorMessage: string;
}