#ifndef MEMORY_HELPER_H
#define MEMORY_HELPER_H

#include <vector>
#include <map>
#include <string>
#include <unistd.h>
#include <sys/uio.h>

struct MemoryPatch {
    uintptr_t address;
    int original_value;
    int patch_value;
    bool active;
};

class MemoryHelper {
public:
    MemoryHelper();
    ~MemoryHelper();

    // Attach to the game process by package name
    bool attach(const std::string& packageName);

    // Read memory at address
    bool readInt(uintptr_t address, int& value);

    // Write memory at address
    bool writeInt(uintptr_t address, int value);

    // Apply the Aimbot patch based on the user's logic
    void setAimbotActive(bool active);

    // Initialize patches (offsets)
    void initializePatches();

    // Get current process ID
    pid_t getPid() const { return pid; }

private:
    pid_t pid;
    std::vector<MemoryPatch> patches;
    
    // Internal helper to find process ID
    pid_t findPid(const std::string& packageName);
};

#endif // MEMORY_HELPER_H
