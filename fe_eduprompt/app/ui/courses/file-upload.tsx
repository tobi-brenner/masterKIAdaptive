'use client'
import {useCallback, useState} from 'react';
import {useDropzone} from 'react-dropzone';
import {uploadFilesToAPI} from "@/app/lib/utils/api";

interface FileUploadProps {
    uploadEndpoint: string;
    multiple?: boolean;
    onUploadSuccess?: (message: string) => void;
    onUploadError?: (error: any) => void;
}

const FileUpload = ({uploadEndpoint, multiple = true, onUploadSuccess, onUploadError}: FileUploadProps) => {
    const [uploadedFiles, setUploadedFiles] = useState<File[]>([]);

    const onDrop = useCallback((acceptedFiles: File[]) => {
        setUploadedFiles(acceptedFiles);

        const formData = new FormData();
        const fileNames = acceptedFiles.map(file => file.name).join(',');

        acceptedFiles.forEach((file) => {
            formData.append('files', file);
        });
        formData.append('fileNames', fileNames); // Ensure fileNames are included

        uploadFilesToAPI<{ message: string }>(uploadEndpoint, { body: formData })
            .then(data => {
                if (onUploadSuccess) {
                    onUploadSuccess(data.message);
                }
            })
            .catch(error => {
                if (onUploadError) {
                    onUploadError(error);
                }
            });
    }, [uploadEndpoint, onUploadSuccess, onUploadError]);

    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop, multiple});

    return (
        <div>
            <div {...getRootProps()} className="dropzone p-4 border-2 border-dashed border-gray-400 rounded-md">
                <input {...getInputProps()} />
                {isDragActive ? (
                    <p>Hier die Dateien hochladen</p>
                ) : (
                    <p>Kursmaterialien hier hochladen.</p>
                )}
            </div>
            <div className="mt-4">
                {uploadedFiles.length > 0 && (
                    <div>
                        <h4 className="font-medium">Uploaded Files:</h4>
                        <ul>
                            {uploadedFiles.map((file, index) => (
                                <li key={index} className="text-sm">
                                    {file.name}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FileUpload;
