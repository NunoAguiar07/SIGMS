import {RegisterScreenType} from "../types/RegisterScreenType";
import {UniversityInterface} from "../../types/UniversityInterface";
import {isMobile} from "../../utils/DeviceType";
import {Button, ButtonsContainer, ButtonText} from "../css_styling/common/Buttons";
import {Card, CenteredContainer, ColumnContainer, Container} from "../css_styling/common/NewContainers";
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
}: RegisterScreenType) => {
    return (
        <CenteredContainer flex={1}>
            <Card shadow="medium" padding="huge" gap="lg">
                <Title color="black">Register</Title>

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
            </Card>
        </CenteredContainer>
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
    <Container flexDirection={isMobile ? 'column' : 'row'} gap="md">
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
    </Container>
);

interface RolePickerType {
    role: string;
    onRoleChange: (itemValue: string) => void;
}

export const RolePicker = ({ role, onRoleChange }: RolePickerType) => (
    <ColumnContainer gap="md">
        <BodyText textAlign="center">Select your role:</BodyText>
        <PickerContainer width="50%">
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
    <ColumnContainer gap="md" style={{ position: 'relative' , zIndex: 10}}>
        <BodyText textAlign="center">Search for your university:</BodyText>
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
}

export const AuthButtons = ({ onRegister, onNavigateToLogin }: AuthButtonsType) => (
    <ButtonsContainer gap="md" flexDirection={isMobile ? 'column' : 'row'} alignItems="center" justifyContent="center">
        <Button
            variant="primary"
            onPress={onRegister}
            style={isMobile ? { width: '100%' } : { flex: 1 }}
        >
            <ButtonText>Register</ButtonText>
        </Button>
        <Button
            variant="primary"
            onPress={onNavigateToLogin}
            style={isMobile ? { width: '100%' } : { flex: 1 }}
        >
            <ButtonText>Go to Login</ButtonText>
        </Button>
    </ButtonsContainer>
);