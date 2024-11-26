import boto3

# Initialize a session using your profile and region
session = boto3.Session( region_name="ap-south-1")

# Create EC2 client
ec2_client = session.client('ec2')


def get_available_volumes_grouped_by_tessell_user_id():
    # Describe volumes
    response = ec2_client.describe_volumes(
        Filters=[
            {
                'Name': 'status',
                'Values': ['available']
            }
        ]
    )

    # Dictionary to group volumes by TESSELL_USER_ID
    volumes_dict = {}
    # Process each volume
    for volume in response['Volumes']:
        volume_info = {
            'VolumeId': volume['VolumeId'],
            'Size': volume['Size'],  # Volume size in GiB
            'AvailabilityZone': volume['AvailabilityZone'],
        }

        # Check for TESSELL_USER_ID tag
        tessell_user_id = 'unauthorized'  # Default value if TESSELL_USER_ID is not found
        if 'Tags' in volume:
            for tag in volume['Tags']:
                if tag['Key'] == 'TESSELL_USER_ID':
                    tessell_user_id = tag['Value']
                    break

        # Initialize entry if TESSELL_USER_ID or 'unauthorized' is not already in the dictionary
        if tessell_user_id not in volumes_dict:
            volumes_dict[tessell_user_id] = []

        # Add the volume to the corresponding TESSELL_USER_ID or 'unauthorized' key
        volumes_dict[tessell_user_id].append(volume_info)

    return volumes_dict


if __name__ == "__main__":
    available_vols_by_user_id = get_available_volumes_grouped_by_tessell_user_id()

    # Print out volumes grouped by TESSELL_USER_ID
    for user_id, volumes in available_vols_by_user_id.items():
        print(f"TESSELL_USER_ID: {user_id}")
        for vol in volumes:
            print(
                f"  - VolumeId: {vol['VolumeId']}, Size: {vol['Size']} GiB, AvailabilityZone: {vol['AvailabilityZone']}")
