import {RegisterScreenType} from "../types/RegisterScreenType";
import {UniversityInterface} from "../../types/UniversityInterface";
import {isMobile} from "../../utils/DeviceType";
import {Button, ButtonsContainer, ButtonText} from "../css_styling/common/Buttons";
import {Card, CenteredContainer, ColumnContainer, RowContainer} from "../css_styling/common/NewContainers";
import {Input, SearchInput} from "../css_styling/common/Inputs";
import {BodyText, Title} from "../css_styling/common/Typography";
import {PickerContainer, StyledPicker, StyledPickerItem} from "../css_styling/common/Picker";
import {FlatListContainer, FlatListItem} from "../css_styling/common/FlatList";
import {FlatList} from "react-native";

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
    passwordValidation,
    isFormValid
}: RegisterScreenType) => {
    return (
        <CenteredContainer flex={1}>
            <Card shadow="medium" padding="sm" gap="md">
                <Title color="black">Register</Title>

                <AuthFormFields
                    email={email}
                    username={username}
                    password={password}
                    onEmailChange={onEmailChange}
                    onUsernameChange={onUsernameChange}
                    onPasswordChange={onPasswordChange}
                />

                <PasswordRequirements
                    validation={passwordValidation}
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
                    isRegisterDisabled={!isFormValid}
                />
            </Card>
        </CenteredContainer>
    );
}

interface PasswordRequirementsType {
    validation: {
        minLength: boolean;
        hasUppercase: boolean;
        hasLowercase: boolean;
        hasNumber: boolean;
        hasSpecialChar: boolean;
    };
}

export const PasswordRequirements = ({validation }: PasswordRequirementsType) => {
    const requirements = [
        { key: 'minLength', text: 'At least 8 characters', met: validation.minLength },
        { key: 'hasUppercase', text: 'One uppercase letter', met: validation.hasUppercase },
        { key: 'hasLowercase', text: 'One lowercase letter', met: validation.hasLowercase },
        { key: 'hasNumber', text: 'One number', met: validation.hasNumber },
        { key: 'hasSpecialChar', text: 'One special character', met: validation.hasSpecialChar }
    ];

    return (
        <ColumnContainer gap="sm">
            <BodyText textAlign="center" family="bold">Password Requirements:</BodyText>
            {requirements.map((req) => (
                <RowContainer key={req.key} gap="sm" alignItems="center">
                    <BodyText
                        color={req.met ? 'green' : 'red'}
                        family={req.met ? 'bold' : 'regular'}
                    >
                        {req.met ? '✓' : '✗'}
                    </BodyText>
                    <BodyText
                        color={req.met ? 'green' : 'red'}
                        style={req.met ? { textDecorationLine: 'line-through' } : {}}
                    >
                        {req.text}
                    </BodyText>
                </RowContainer>
            ))}
        </ColumnContainer>
    );
};

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
    <CenteredContainer flexDirection={isMobile ? 'column' : 'row'} gap="md">
        <Input
            placeholder="Email"
            value={email}
            onChangeText={onEmailChange}
            autoCapitalize="none"
            keyboardType="email-address"
        />
        <Input
            placeholder="Username"
            value={username}
            onChangeText={onUsernameChange}
            autoCapitalize="none"
        />
        <Input
            placeholder="Password"
            value={password}
            onChangeText={onPasswordChange}
            secureTextEntry
            autoCapitalize="none"
        />
    </CenteredContainer>
);

interface RolePickerType {
    role: string;
    onRoleChange: (itemValue: string) => void;
}

export const RolePicker = ({ role, onRoleChange }: RolePickerType) => (
    <ColumnContainer gap="md">
        <BodyText textAlign="center" family="bold">Select your role:</BodyText>
        <PickerContainer width="200px">
            <StyledPicker
                selectedValue={role}
                onValueChange={(itemValue) => onRoleChange(itemValue as string)}
                dropdownIconColor="#666"
                mode="dropdown"
            >
                <StyledPickerItem label="Student" value="STUDENT" />
                <StyledPickerItem label="Teacher" value="TEACHER" />
                <StyledPickerItem label="Technical Services" value="TECHNICAL_SERVICE" />
            </StyledPicker>
        </PickerContainer>
    </ColumnContainer>
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
    <ColumnContainer gap="md" style={{ position: 'relative' , zIndex: 10}} justifyContent="center" alignItems="center">
        <BodyText textAlign="center" family="bold">Search for your university:</BodyText>
        <SearchInput
            placeholder="Search universities..."
            value={searchQuery}
            onChangeText={onSearchChange}
        />
        {universities.length > 0 && (
            <FlatListContainer>
                <FlatList
                    data={universities}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <FlatListItem
                            onPress={() => onUniversitySelect(item)}
                        >
                            <BodyText>{item.name}</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        )}
    </ColumnContainer>
);

interface AuthButtonsType {
    onRegister: () => void;
    onNavigateToLogin: () => void;
    isRegisterDisabled: boolean;
}

export const AuthButtons = ({ onRegister, onNavigateToLogin, isRegisterDisabled }: AuthButtonsType) => (
    <ButtonsContainer gap="sm" flexDirection={'row'} justifyContent="center">
        <Button
            variant="primary"
            onPress={onRegister}
            disabled={isRegisterDisabled}
        >
            <ButtonText>Register</ButtonText>
        </Button>
        <Button
            variant="primary"
            onPress={onNavigateToLogin}
        >
            <ButtonText>Go to Login</ButtonText>
        </Button>
    </ButtonsContainer>
);