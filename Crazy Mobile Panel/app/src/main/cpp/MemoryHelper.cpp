#include "MemoryHelper.h"
#include <iostream>
#include <fstream>
#include <dirent.h>
#include <cstring>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

MemoryHelper::MemoryHelper() : pid(-1) {}

MemoryHelper::~MemoryHelper() {}

pid_t MemoryHelper::findPid(const std::string& packageName) {
    DIR* dir = opendir("/proc");
    if (!dir) return -1;

    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        if (entry->d_type != DT_DIR) continue;

        char* endptr;
        long pid_long = strtol(entry->d_name, &endptr, 10);
        if (*endptr != '\0') continue;

        std::string cmdlinePath = std::string("/proc/") + entry->d_name + "/cmdline";
        std::ifstream cmdlineFile(cmdlinePath);
        std::string cmdline;
        if (cmdlineFile >> cmdline) {
            if (cmdline.find(packageName) != std::string::npos) {
                closedir(dir);
                return static_cast<pid_t>(pid_long);
            }
        }
    }

    closedir(dir);
    return -1;
}

bool MemoryHelper::attach(const std::string& packageName) {
    pid = findPid(packageName);
    if (pid != -1) {
        initializePatches(); // Load offsets when attached
        return true;
    }
    return false;
}

void MemoryHelper::initializePatches() {
    // Clear existing patches
    patches.clear();

    // ==========================================
    // ADD YOUR GAME OFFSETS HERE
    // Example: Aimbot (Head) Patch
    // ==========================================
    
    // MemoryPatch aimbotPatch;
    // aimbotPatch.address = 0x12345678; // Your Address
    // aimbotPatch.original_value = 100; // Original Game Value
    // aimbotPatch.patch_value = 1;      // Headshot Value
    // aimbotPatch.active = false;
    // patches.push_back(aimbotPatch);

    // Add more patches for Landing, etc. as needed
}

bool MemoryHelper::readInt(uintptr_t address, int& value) {
    if (pid == -1) return false;

    struct iovec local[1];
    struct iovec remote[1];

    local[0].iov_base = &value;
    local[0].iov_len = sizeof(int);
    remote[0].iov_base = (void*)address;
    remote[0].iov_len = sizeof(int);

    ssize_t nread = process_vm_readv(pid, local, 1, remote, 1, 0);
    return nread == sizeof(int);
}

bool MemoryHelper::writeInt(uintptr_t address, int value) {
    if (pid == -1) return false;

    struct iovec local[1];
    struct iovec remote[1];

    local[0].iov_base = &value;
    local[0].iov_len = sizeof(int);
    remote[0].iov_base = (void*)address;
    remote[0].iov_len = sizeof(int);

    ssize_t nwrite = process_vm_writev(pid, local, 1, remote, 1, 0);
    return nwrite == sizeof(int);
}

void MemoryHelper::setAimbotActive(bool active) {
    if (pid == -1) return;

    for (auto& patch : patches) {
        if (active && !patch.active) {
            // Apply patch: Write headlValue
            if (writeInt(patch.address, patch.patch_value)) {
                patch.active = true;
                std::cout << "Applied Aimbot at: " << std::hex << patch.address << std::endl;
            }
        } else if (!active && patch.active) {
            // Restore original: Write originalValue
            if (writeInt(patch.address, patch.original_value)) {
                patch.active = false;
                std::cout << "Restored Memory at: " << std::hex << patch.address << std::endl;
            }
        }
    }
}
