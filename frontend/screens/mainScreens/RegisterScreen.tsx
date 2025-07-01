import {FlatList, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {commonStyles} from "../css_styling/common/CommonProps";
import {universityStyles} from "../css_styling/university/UniversityProps";
import {Picker} from '@react-native-picker/picker';
import {RegisterScreenType} from "../types/RegisterScreenType";
import {UniversityInterface} from "../../types/UniversityInterface";
import {isMobile} from "../../utils/DeviceType";

export const RegisterScreen = ({
    email,
    username,
    password,
    role,
    universities,
    searchQuery,
    onEmailChange,
    onUsernameChange,
    onPasswordChange,
    onRoleChange,
    onSearchChange,
    onUniversitySelect,
    onRegister,
    onNavigateToLogin,
}: RegisterScreenType) => {
    return (
        <View style={commonStyles.container}>
            <View style={commonStyles.card}>
                <Text style={commonStyles.title}>Register</Text>

                <AuthFormFields
                    email={email}
                    username={username}
                    password={password}
                    onEmailChange={onEmailChange}
                    onUsernameChange={onUsernameChange}
                    onPasswordChange={onPasswordChange}
                />

                <RolePicker role={role} onRoleChange={onRoleChange} />

                <UniversitySearch
                    searchQuery={searchQuery}
                    universities={universities}
                    onSearchChange={onSearchChange}
                    onUniversitySelect={onUniversitySelect}
                />

                <AuthButtons
                    onRegister={onRegister}
                    onNavigateToLogin={onNavigateToLogin}
                />
            </View>
        </View>
    );
}

interface AuthFormFieldsType {
    email: string;
    username: string;
    password: string;
    onEmailChange: (text: string) => void;
    onUsernameChange: (text: string) => void;
    onPasswordChange: (text: string) => void;
}

export const AuthFormFields = ({
    email,
    username,
    password,
    onEmailChange,
    onUsernameChange,
    onPasswordChange
}: AuthFormFieldsType) => (
    <View style={!isMobile ? commonStyles.inputRow : commonStyles.inputColumn}>
        <TextInput
            style={[commonStyles.searchInput, !isMobile ? commonStyles.inputRowItem : commonStyles.inputColumnItem]}
            placeholder="Email"
            value={email}
            onChangeText={onEmailChange}
            autoCapitalize="none"
            keyboardType="email-address"
        />
        <TextInput
            style={[commonStyles.searchInput, !isMobile ? commonStyles.inputRowItem : commonStyles.inputColumnItem]}
            placeholder="Username"
            value={username}
            onChangeText={onUsernameChange}
            autoCapitalize="none"
        />
        <TextInput
            style={[commonStyles.searchInput, !isMobile ? commonStyles.inputRowItem : commonStyles.inputColumnItem]}
            placeholder="Password"
            value={password}
            onChangeText={onPasswordChange}
            secureTextEntry
            autoCapitalize="none"
        />
    </View>
);

interface RolePickerType {
    role: string;
    onRoleChange: (itemValue: string) => void;
}

export const RolePicker = ({ role, onRoleChange }: RolePickerType) => (
    <>
        <Text style={commonStyles.centerMiddleText}>Select your role:</Text>
        <View style={commonStyles.pickerContainer}>
            <Picker
                selectedValue={role}
                onValueChange={onRoleChange}
                style={commonStyles.picker}
                dropdownIconColor="#666"
                mode="dropdown"
            >
                <Picker.Item label="Student" value="STUDENT" />
                <Picker.Item label="Teacher" value="TEACHER" />
                <Picker.Item label="Technical Services" value="TECHNICAL_SERVICE" />
            </Picker>
        </View>
    </>
);

interface UniversitySearchType {
    searchQuery: string;
    universities: UniversityInterface[];
    onSearchChange: (text: string) => void;
    onUniversitySelect: (university: UniversityInterface) => void;
}

export const UniversitySearch = ({
    searchQuery,
    universities,
    onSearchChange,
    onUniversitySelect
}: UniversitySearchType) => (
    <View style={universityStyles.universitySearchContainer}>
        <Text style={commonStyles.centerMiddleText}>Search for your university:</Text>
        <TextInput
            style={commonStyles.searchInput}
            placeholder="Search universities..."
            value={searchQuery}
            onChangeText={onSearchChange}
        />
        {universities.length > 0 && (
            <View style={universityStyles.universityResultsContainer}>
                <FlatList
                    data={universities}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <TouchableOpacity
                            style={commonStyles.itemSearch}
                            onPress={() => onUniversitySelect(item)}
                        >
                            <Text style={commonStyles.itemText}>{item.name}</Text>
                        </TouchableOpacity>
                    )}
                />
            </View>
        )}
    </View>
);

interface AuthButtonsType {
    onRegister: () => void;
    onNavigateToLogin: () => void;
}

export const AuthButtons = ({ onRegister, onNavigateToLogin }: AuthButtonsType) => (
    <View style={commonStyles.buttonsContainer}>
        <TouchableOpacity
            style={commonStyles.loginRegisterButton}
            onPress={onRegister}
        >
            <Text style={commonStyles.loginRegisterButtonText}>Register</Text>
        </TouchableOpacity>
        <TouchableOpacity
            style={commonStyles.loginRegisterButton}
            onPress={onNavigateToLogin}
        >
            <Text style={commonStyles.loginRegisterButtonText}>Go to Login</Text>
        </TouchableOpacity>
    </View>
);