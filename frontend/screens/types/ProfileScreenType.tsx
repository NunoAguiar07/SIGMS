import {ProfileInterface} from "../../types/ProfileInterface";

export interface ProfileScreenType {
    profile: ProfileInterface;
    image: string| null;
    onPickImage: () => void;
}