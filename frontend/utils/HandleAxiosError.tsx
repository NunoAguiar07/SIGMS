import axios from 'axios';
import {ParsedError} from "../types/errors/ParseErrorTypes";



/**
 * Handles errors from Axios requests and returns a standardized error object.
 *
 * @param {any} error - The error object from the Axios request.
 * @returns {ParsedError} A standardized error object containing status and message.
 */
export const handleAxiosError = (error: any): ParsedError => {
    if (axios.isAxiosError(error)) {
        if (error.response) {
            return {
                status: error.response.status,
                message: error.response.data?.detail || 'Request failed',
            };
        } else if (error.request) {
            return {
                status: 500,
                message: 'No response received from server'
            };
        } else {
            return {
                status: 500,
                message: error.message
            };
        }
    } else {
        return {
            status: 500,
            message: 'An unexpected error occurred'
        };
    }
};