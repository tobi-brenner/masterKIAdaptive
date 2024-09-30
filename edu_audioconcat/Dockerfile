# FROM python:3.8-slim

# WORKDIR /app

# COPY ./requirements.txt /app/requirements.txt

# RUN apt-get update \
#     && apt-get install gcc -y \
#     && apt-get clean

# RUN pip install -r /app/requirements.txt \
#     && rm -rf /root/.cache/pip

# COPY . /app/


FROM python:3.9
WORKDIR /code
COPY ./requirements.txt /code/requirements.txt
RUN apt-get -y update && apt-get -y upgrade && apt-get install -y --no-install-recommends ffmpeg
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt
COPY ./app /code/app

CMD ["fastapi", "run", "app/main.py", "--port", "80"]




# ############## with ffmpeg #################

# # Using Python 3.8-slim as base image
# FROM python:3.8-slim

# # Set the working directory in the container
# WORKDIR /app

# # Copy the requirements file into the container
# COPY ./requirements.txt /app/requirements.txt

# RUN apt-get update \
#     && apt-get install gcc -y \
#     && apt-get clean


# # Update package list, install dependencies, and clean up in one layer to keep image size small
# # RUN apt-get update && \
# #     apt-get install -y gcc ffmpeg && \
# #     apt-get clean && \
# #     rm -rf /var/lib/apt/lists/*

# ##############
# # If ABOVE DOESNT WORK MAYBE THIS
# # RUN set -x \
# #     && add-apt-repository ppa:mc3man/trusty-media \
# #     && apt-get update \
# #     && apt-get dist-upgrade \
# #     && apt-get install -y --no-install-recommends \
# #         ffmpeg \ 
# ##############

# # Install Python dependencies
# RUN pip install --no-cache-dir -r /app/requirements.txt

# # Copy the rest of the application into the container
# COPY . /app/

