import SwiftUI
import AVFoundation

struct ContentView: View {
    @StateObject private var photoManager = PhotoManager()
    @State private var showingCamera = false
    @State private var showingPhotoDetail = false
    @State private var selectedPhoto: PhotoItem?

    var body: some View {
        NavigationView {
            ScrollView {
                LazyVGrid(columns: [
                    GridItem(.flexible()),
                    GridItem(.flexible()),
                    GridItem(.flexible())
                ], spacing: 2) {
                    ForEach(photoManager.photos) { photo in
                        AsyncImage(url: photo.thumbnailURL) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: 120, height: 120)
                                .clipped()
                        } placeholder: {
                            Rectangle()
                                .fill(Color.gray.opacity(0.3))
                                .frame(width: 120, height: 120)
                        }
                        .onTapGesture {
                            selectedPhoto = photo
                            showingPhotoDetail = true
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("PhotoShare")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showingCamera = true
                    }) {
                        Image(systemName: "camera.fill")
                    }
                }
            }
            .sheet(isPresented: $showingCamera) {
                CameraView(photoManager: photoManager)
            }
            .sheet(isPresented: $showingPhotoDetail) {
                if let photo = selectedPhoto {
                    PhotoDetailView(photo: photo, photoManager: photoManager)
                }
            }
            .onAppear {
                photoManager.requestPermissions()
            }
        }
    }
}

struct CameraView: UIViewControllerRepresentable {
    let photoManager: PhotoManager
    @Environment(\.presentationMode) var presentationMode

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .camera
        picker.cameraDevice = .rear
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: CameraView

        init(_ parent: CameraView) {
            self.parent = parent
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let image = info[.originalImage] as? UIImage {
                parent.photoManager.capturePhoto(image: image)
            }
            parent.presentationMode.wrappedValue.dismiss()
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.presentationMode.wrappedValue.dismiss()
        }
    }
}

struct PhotoDetailView: View {
    let photo: PhotoItem
    let photoManager: PhotoManager
    @Environment(\.presentationMode) var presentationMode
    @State private var showingShareSheet = false

    var body: some View {
        NavigationView {
            VStack {
                AsyncImage(url: photo.fullURL) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                } placeholder: {
                    ProgressView()
                }

                Spacer()

                VStack(spacing: 16) {
                    if let location = photo.location {
                        HStack {
                            Image(systemName: "location.fill")
                                .foregroundColor(.blue)
                            Text("Photo taken at location")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }

                    HStack(spacing: 20) {
                        Button("Share") {
                            photoManager.sharePhoto(photo)
                        }
                        .buttonStyle(.borderedProminent)

                        Button("Save to Photos") {
                            photoManager.exportToPhotos(photo)
                        }
                        .buttonStyle(.bordered)
                    }
                }
                .padding()
            }
            .navigationTitle("Photo")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(trailing: Button("Done") {
                presentationMode.wrappedValue.dismiss()
            })
        }
    }
}
