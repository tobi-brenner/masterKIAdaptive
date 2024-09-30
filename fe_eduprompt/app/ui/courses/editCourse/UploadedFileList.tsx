

'use client';
import { BE_BASE_URL } from '@/constants';
import React from 'react';

interface FileListProps {
  files: string[];
}

const UploadedFileList: React.FC<FileListProps> = ({ files }) => {
  const baseUrl = `${BE_BASE_URL}/files/download`;

  return (
    <div className="mb-6">
      <h2 className="mb-4 text-lg font-semibold">Hochgeladene Dateien</h2>
      <ul>
        {files.map((file, index) => (
          <li key={index} className="text-sm">
            <a
              href={`${baseUrl}/${file}`}
              download
              className="text-blue-500 hover:underline"
            >
              {file}
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default UploadedFileList;
