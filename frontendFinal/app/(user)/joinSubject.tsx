// const joinSubject = () => {
//     const [subjects, setSubjects] = useState<SubjectInterface | null>(null);
//     const [searchQuery, setSearchQuery] = useState('');
//     const [error, setError] = useState<ErrorInterface | null>(null);
//
//     useEffect(() => {
//         const fetchData = SubjectsRequest(setSubjects, setError);
//         fetchData();
//     }, []);
//
//     // Handle errors
//     if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
//
//     // Show loading while data is being fetched
//     if (!subjects) return <LoadingPresentation />;
//     return <JoinSubjectScreen subjects ={subjects} setSubjects = {setSubjects} />;
// }
//
// export default joinSubject;