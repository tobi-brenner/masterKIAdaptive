"use client";
import React from 'react';
import FileUpload from '@/app/ui/courses/file-upload';

interface FileUploadSectionProps {
    onUploadSuccess: (message: string) => void;
    onUploadError: (error: any) => void;
    courseId: number;
}

const FileUploadSection: React.FC<FileUploadSectionProps> = ({ onUploadSuccess, onUploadError, courseId }) => (
    <div className="mb-6">
        <h2 className="text-lg font-semibold mb-4">Dateien hochladen</h2>
        <FileUpload
            uploadEndpoint={`/files/uploads/${courseId}`}
            multiple
            onUploadSuccess={onUploadSuccess}
            onUploadError={onUploadError}
        />
    </div>
);

export default FileUploadSection;
