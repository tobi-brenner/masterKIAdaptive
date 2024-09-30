from typing import Union, Annotated
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import FileResponse
from pydub import AudioSegment
import os
import io



app = FastAPI()


# @app.post("/files/")
# async def concat_file(file: Annotated[bytes, File()]):
#     return {"file_size": len(file)}

# @app.post("/uploadfile/")
# async def concat_ufile(file: UploadFile):
#     return {"file_size": file.filename}


# @app.post("/mfiles/")
# async def create_files(files: Annotated[list[bytes], File()]):
#     return {"file_sizes": [len(file) for file in files]}


@app.post("/genaudio/")
async def create_upload_files(files: list[UploadFile]):
    output_file = merge_audio(files)
    return FileResponse(output_file, media_type='audio/mpeg', filename=output_file)
    # return {"filenames": [file.filename for file in files]}

@app.get("/")
async def read_root():
    return {"Hello": "World go to /docs"}

 
# merge two audio
def merge_audio(files: list[UploadFile]):
    combined = AudioSegment.empty()  # Start with an empty audio segment

    for file in files:
        # Read the file content into an audio segment
        content = file.file.read()
        audio = AudioSegment.from_file(io.BytesIO(content), format="mp3")
        combined += audio  # Concatenate each file to the combined audio
        file.file.seek(0)  # Reset file pointer

    output_path = "combined.mp3"
    combined.export(output_path, format="mp3")
    return output_path


    # Export combined audio to disk
    # combined.export("combined.mp3", format="mp3")