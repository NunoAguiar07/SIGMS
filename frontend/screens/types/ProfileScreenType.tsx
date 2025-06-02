import {ProfileInterface} from "../../types/ProfileInterface";

export interface ProfileScreenType {
    profile: ProfileInterface;
    onUpdateProfile: (profileData: Partial<ProfileInterface>) => Promise<ProfileInterface>;
}